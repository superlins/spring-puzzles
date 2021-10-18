package org.example.hbase;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renc
 */
@ConfigurationProperties("spring.data.hbase")
public class HBaseProperties {

    /** Name of ZooKeeper quorum configuration parameter, as "hbase.zookeeper.quorum" */
    private String quorum;

    /** Name of ZooKeeper port configuration parameter, as "hbase.zookeeper.property.clientPort" */
    private String port = HBaseConstants.DEFAULT_ZOOKEEPER_CLIENT_PORT;

    /** Parameter name for the root dir in ZK for this cluster, as "zookeeper.znode.parent" */
    private String znodeParent = HBaseConstants.DEFAULT_ZOOKEEPER_ZNODE_PARENT;

    /** Parameter name for HBase instance root directory, as "hbase.rootdir" */
    private String rootDir = HBaseConstants.DEFAULT_HBASE_DIR;

    /** Authentication method as KERBEROS enabled */
    private boolean authEnabled = false;

    /** Authentication method as KERBEROS user principal */
    private String principal;

    /** Authentication method as KERBEROS keytab file path */
    private String keytabFile;

    private final Map<String, String> properties = new HashMap<>();

    public String getQuorum() {
        return quorum;
    }

    public void setQuorum(String quorum) {
        this.quorum = quorum;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getZnodeParent() {
        return znodeParent;
    }

    public void setZnodeParent(String znodeParent) {
        this.znodeParent = znodeParent;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public void setAuthEnabled(boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getKeytabFile() {
        return keytabFile;
    }

    public void setKeytabFile(String keytabFile) {
        this.keytabFile = keytabFile;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
