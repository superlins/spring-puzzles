package org.example.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import java.io.IOException;

import static org.example.hbase.HBaseConstants.*;

/**
 * @author renc
 */
@ConditionalOnClass(HBaseTemplate.class)
@EnableConfigurationProperties(HBaseProperties.class)
public class HBaseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HBaseTemplate.class)
    public HBaseTemplate hbaseTemplate(HBaseProperties hBaseProperties) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        propertyMapper.from(hBaseProperties::getPort).to(port -> conf.set(ZOOKEEPER_CLIENT_PORT, port));
        propertyMapper.from(hBaseProperties::getQuorum).to(quorum -> conf.set(ZOOKEEPER_QUORUM, quorum));
        propertyMapper.from(hBaseProperties::getRootDir).to(rootDir -> conf.set(HBASE_DIR, rootDir));
        propertyMapper.from(hBaseProperties::getZnodeParent).to(znodeParent -> conf.set(ZOOKEEPER_ZNODE_PARENT, znodeParent));
        propertyMapper.from(hBaseProperties::getProperties).to(props -> props.forEach((k, v) -> conf.set(k, v)));

        if (hBaseProperties.isAuthEnabled()) {
            UserGroupInformation.setConfiguration(conf);
            String principal = hBaseProperties.getPrincipal();
            Assert.notNull(principal, "principal must not be null where kerberos auth enabled");
            String keytabFile = hBaseProperties.getKeytabFile();
            Assert.notNull(keytabFile, "keytab must not be null where kerberos auth enabled");
            UserGroupInformation.loginUserFromKeytab(principal, keytabFile);
        }

        return new HBaseTemplate(conf);
    }
}
