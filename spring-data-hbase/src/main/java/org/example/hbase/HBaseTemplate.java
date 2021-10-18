package org.example.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.ipc.RpcClientFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.PoolMap;
import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author renc
 */
public class HBaseTemplate implements HBaseOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseTemplate.class);

    /** Default value for client scanner caching, as "hbase.client.scanner.caching" */
    private static final int DEFAULT_HBASE_CLIENT_SCANNER_CACHING = 5000;

    /** Default value for client scanner caching, as "hbase.client.write.buffer" */
    private static final int DEFAULT_WRITE_BUFFER_SIZE = 3 * 1024 * 1024;

    private final Configuration configuration;

    private volatile Connection singleton;

    public HBaseTemplate(Configuration configuration) {
        Assert.notNull(configuration, "configuration is required non null");
        this.configuration = configuration;
    }

    @Override
    public <T> List<T> find(String tableName, String family, RowMapper<T> mapper) {
        Scan scan = new Scan();
        scan.setCaching(DEFAULT_HBASE_CLIENT_SCANNER_CACHING);
        scan.addFamily(Bytes.toBytes(family));
        return this.find(tableName, scan, mapper);
    }

    @Override
    public <T> List<T> find(String tableName, String family, String qualifier, RowMapper<T> mapper) {
        Scan scan = new Scan();
        scan.setCaching(DEFAULT_HBASE_CLIENT_SCANNER_CACHING);
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this.find(tableName, scan, mapper);
    }

    @Override
    public <T> List<T> find(String tableName, Scan scan, RowMapper<T> mapper) {
        return this.execute(tableName, table -> {
            int caching = scan.getCaching();
            if (caching == 1) {
                scan.setCaching(DEFAULT_HBASE_CLIENT_SCANNER_CACHING);
            }
            ResultScanner scanner = table.getScanner(scan);
            try {
                List<T> rs = new ArrayList<>();
                int rowNum = 0;
                for (Result result : scanner) {
                    rs.add(mapper.mapRow(result, rowNum++));
                }
                return rs;
            } finally {
                scanner.close();
            }
        });
    }

    @Override
    public <T> T get(String tableName, String rowName, RowMapper<T> mapper) {
        return this.get(tableName, rowName, null, null, mapper);
    }

    @Override
    public <T> T get(String tableName, String rowName, String familyName, RowMapper<T> mapper) {
        return this.get(tableName, rowName, familyName, null, mapper);
    }

    @Override
    public <T> T get(String tableName, String rowName, String familyName, String qualifier, RowMapper<T> mapper) {
        return this.execute(tableName, table -> {
            Get get = new Get(Bytes.toBytes(rowName));
            if (StringUtils.hasText(familyName)) {
                byte[] family = Bytes.toBytes(familyName);
                if (StringUtils.hasText(qualifier)) {
                    get.addColumn(family, Bytes.toBytes(qualifier));
                } else {
                    get.addFamily(family);
                }
            }
            Result result = table.get(get);
            return mapper.mapRow(result, 0);
        });
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "no table specified");

        Table table = null;
        try {
            table = this.connection().getTable(TableName.valueOf(tableName));
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throw new HBaseSystemException(throwable);
        } finally {
            if (null != table) {
                try {
                    table.close();
                } catch (IOException e) {
                    LOGGER.error("hbase resources release failed");
                }
            }
        }
    }

    @Override
    public void execute(String tableName, MutatorCallback action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "no table specified");

        BufferedMutator mutator = null;
        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
            mutator = this.connection().getBufferedMutator(mutatorParams.writeBufferSize(DEFAULT_WRITE_BUFFER_SIZE));
            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            throw new HBaseSystemException(throwable);
        } finally {
            if (null != mutator) {
                try {
                    mutator.flush();
                    mutator.close();
                } catch (IOException e) {
                    LOGGER.error("hbase mutator resources release failed");
                }
            }
        }
    }

    @Override
    public void mutate(String tableName, Mutation mutation) {
        this.execute(tableName, mutator -> {
            mutator.mutate(mutation);
        });
    }

    @Override
    public void mutate(String tableName, List<? extends Mutation> mutations) {
        this.execute(tableName, mutator -> {
            mutator.mutate(mutations);
        });
    }

    /**
     * Retrieve hbase connection with customize executor.
     *
     * @see ClusterConnection
     * @see RpcClientFactory
     * @see PoolMap
     * @see HTable#getDefaultExecutor(Configuration)
     * @return a singleton hbase connection
     */
    public Connection connection() {
        if (null == this.singleton) {
            synchronized (this) {
                if (null == this.singleton) {
                    ThreadPoolExecutor executor = defaultExecutor();
                    try {
                        this.singleton = ConnectionFactory.createConnection(configuration, executor);
                    } catch (IOException e) {
                        executor.shutdownNow();
                        throw new HBaseSystemException("hbase connection resources poll create failed", e);
                    }
                }
            }
        }
        return this.singleton;
    }

    private ThreadPoolExecutor defaultExecutor() {
        int maxSize = configuration.getInt(HBaseConstants.HBASE_CONNECTION_THREADS_MAX,
                HBaseConstants.DEFAULT_HBASE_CONNECTION_THREADS_MAX);
        int coreSize = configuration.getInt(HBaseConstants.HBASE_CONNECTION_THREADS_CORE,
                HBaseConstants.DEFAULT_HBASE_CONNECTION_THREADS_CORE);
        long keepAliveTime = configuration.getLong(HBaseConstants.HBASE_CONNECTION_THREADS_KEEPALIVE_TIME,
                HBaseConstants.DEFAULT_HBASE_CONNECTION_THREADS_KEEPALIVE_TIME);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                maxSize,
                coreSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Threads.newDaemonThreadFactory("hconnection-0x" + Integer.toHexString(hashCode()) + "-shared-"));
        executor.prestartCoreThread(); // init
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
