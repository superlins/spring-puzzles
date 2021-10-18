package org.example.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author renc
 */
public class TestHBase {

    public static void main(String[] args) throws IOException {

        String krb5 = "/Users/apple/Nutstore Files/My Nutstore/Resources/ws/IdeaProjects/spring-puzzles/spring-data-hbase/src/main/resources/krb5.conf";
        String principal = "hbase/admin@HADOOP.COM";
        String ketTab = "/Users/apple/Nutstore Files/My Nutstore/Resources/ws/IdeaProjects/spring-puzzles/spring-data-hbase/src/main/resources/hbase.keytab";

        System.setProperty("java.security.krb5.conf", krb5);

        Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, "optimus20a3,optimus20a2,optimus20a1");
        conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT, HConstants.DEFAULT_ZOOKEEPER_ZNODE_PARENT);
        conf.set(HConstants.HBASE_DIR, "/hbase");

        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hbase.security.authentication", "kerberos");
        conf.set("hbase.master.kerberos.principal", "hbase/_HOST@HADOOP.COM");
        conf.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@HADOOP.COM");

        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(principal, ketTab);

        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            System.out.println(connection.isClosed());

            Table table = connection.getTable(TableName.valueOf("tools:md5_phone"));
            Get get = new Get(Bytes.toBytes("2e65029dcb7a861b3f7d1098da6004be"));
            Result result = table.get(get);
            System.out.println(result);

            // >>>>>>>> RESULT: keyvalues={2e65029dcb7a861b3f7d1098da6004be/cf:city/1622102043465/Put/vlen=6/seqid=0, 2e65029dcb7a861b3f7d1098da6004be/cf:isp/1622102043465/Put/vlen=6/seqid=0, 2e65029dcb7a861b3f7d1098da6004be/cf:number/1615042561707/Put/vlen=11/seqid=0, 2e65029dcb7a861b3f7d1098da6004be/cf:province/1622102043465/Put/vlen=6/seqid=0}

            TableName[] tableNames = connection.getAdmin().listTableNames();
            System.out.println(">>>>>>>> TABLE NAMES: " + Arrays.toString(tableNames));

            // >>>>>>>> TABLE NAMES: [tools:md5_phone, tools:sha256_md5]

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
