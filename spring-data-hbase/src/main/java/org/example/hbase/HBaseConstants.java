package org.example.hbase;

import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.util.PoolMap;

/**
 * @author renc
 */
public abstract class HBaseConstants {

    /** Name of ZooKeeper quorum configuration parameter */
    public static final String ZOOKEEPER_QUORUM = HConstants.ZOOKEEPER_QUORUM;

    /** Name of ZooKeeper's clients config port parameter */
    public static final String ZOOKEEPER_CLIENT_PORT = "hbase.zookeeper.property.clientPort";
    public static final String DEFAULT_ZOOKEEPER_CLIENT_PORT = "2181";

    /** Parameter name for the root dir in ZK for this cluster */
    public static final String ZOOKEEPER_ZNODE_PARENT = HConstants.ZOOKEEPER_ZNODE_PARENT;
    public static final String DEFAULT_ZOOKEEPER_ZNODE_PARENT = "/hbase";

    /** Parameter name for HBase instance root directory */
    public static final String HBASE_DIR = HConstants.HBASE_DIR;
    public static final String DEFAULT_HBASE_DIR = "/hbase";

    /** Parameter name for HBase client IPC pool type */
    public static final String HBASE_CLIENT_IPC_POOL_TYPE = HConstants.HBASE_CLIENT_IPC_POOL_TYPE;
    public static final String DEFAULT_HBASE_CLIENT_IPC_POOL_TYPE = PoolMap.PoolType.RoundRobin.name();

    /** Parameter name for HBase client IPC pool size */
    public static final String HBASE_CLIENT_IPC_POOL_SIZE = HConstants.HBASE_CLIENT_IPC_POOL_SIZE;
    public static final int DEFAULT_HBASE_CLIENT_IPC_POOL_SIZE = 1;

    /** Parameter name for HBase connection pool-thread max size */
    public static final String HBASE_CONNECTION_THREADS_MAX = "hbase.hconnection.threads.max";
    public static final int DEFAULT_HBASE_CONNECTION_THREADS_MAX = Runtime.getRuntime().availableProcessors() * 8;

    /** Parameter name for HBase connection pool-thread core size */
    public static final String HBASE_CONNECTION_THREADS_CORE = "hbase.hconnection.threads.core";
    public static final int DEFAULT_HBASE_CONNECTION_THREADS_CORE = Runtime.getRuntime().availableProcessors() * 8;

    /** Parameter name for HBase connection pool-thread keepalive time */
    public static final String HBASE_CONNECTION_THREADS_KEEPALIVE_TIME = "hbase.hconnection.threads.keepalivetime";
    public static final int DEFAULT_HBASE_CONNECTION_THREADS_KEEPALIVE_TIME = 60;

}
