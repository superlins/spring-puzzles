package org.example.ddd.infrastructure.conf.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.io.IOException;
import java.net.URL;

import static java.lang.String.format;
import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

/**
 * @author renc
 */
@Configuration(proxyBeanMethods = false)
public class HibernateSupports {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSupports.class);

    /**
     * The mapping resources (equivalent to {@code <mapping-file>} entries in
     * {@code persistence.xml}) for the persistence unit.
     * <p>
     * Note that mapping resources must be relative to the classpath root, e.g.
     * "META-INF/mappings.xml" or "com/company/repository/mappings.xml", so that
     * they can be loaded through {@code ClassLoader.getResource()}.
     */
    private static final String MAPPING_FILES_NAME_PATTERN = CLASSPATH_ALL_URL_PREFIX + "**/*.hbm.xml";

    private static final String PERSISTENCE_UNIT_ROOT_LOCATION =
            DefaultPersistenceUnitManager.ORIGINAL_DEFAULT_PERSISTENCE_UNIT_ROOT_LOCATION;

    @Bean
    public EntityManagerFactoryBuilderCustomizer entityManagerFactoryBuilderCustomizer() {
        return builder -> builder.setPersistenceUnitPostProcessors(new PersistenceUnitPostProcessor() {

            private ResourcePatternResolver mappingFileResolver = new PathMatchingResourcePatternResolver();

            @Override
            public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
                URL persistenceUnitRootUrl = pui.getPersistenceUnitRootUrl();
                try {
                    Resource[] scannedResources = mappingFileResolver.getResources(MAPPING_FILES_NAME_PATTERN);
                    for (Resource resource : scannedResources) {
                        try {
                            String resourcePath = resource.getURI().getPath();
                            String mappingFile = resourcePath.replace(persistenceUnitRootUrl.getPath(), "");
                            pui.addMappingFileName(mappingFile);

                            LOGGER.debug("Registering classpath-scanned entity mapping file {} in persistence unit info!", resource);
                        } catch (IOException e) {
                            throw new IllegalStateException(format("Couldn't get URI for %s!", resource), e);
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(format(
                            "Cannot load mapping files from pattern %s!", MAPPING_FILES_NAME_PATTERN), e);
                }
            }
        });
    }
}
