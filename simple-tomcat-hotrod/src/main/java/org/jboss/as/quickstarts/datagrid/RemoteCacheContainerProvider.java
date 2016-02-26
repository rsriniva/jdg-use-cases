package org.jboss.as.quickstarts.datagrid;

import java.util.logging.Logger;
import java.util.Properties;
import java.io.IOException;
import java.io.File;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * 
 * {@link CacheContainerProvider}'s implementation creating a HotRod client. 
 * JBoss Data Grid server needs to be running and configured properly 
 * so that HotRod client can remotely connect to it - this is called client-server mode.
 * 
 */
@ApplicationScoped
public class RemoteCacheContainerProvider{
	
	public static final String DATAGRID_HOST = "datagrid.host";
    public static final String HOTROD_PORT = "datagrid.hotrod.port";
    public static final String PROPERTIES_FILE = "META-INF" + File.separator + "datagrid.properties";

    private Logger log = Logger.getLogger(this.getClass().getName());

    private RemoteCacheManager manager;

    public RemoteCacheManager getCacheContainer() {
        if (manager == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addServers(jdgProperty(DATAGRID_HOST));
                 //.host(jdgProperty(DATAGRID_HOST));
                // .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));
            manager = new RemoteCacheManager(builder.build());
            log.info("=== Using RemoteCacheManager (Hot Rod) ===");
        }
        return manager;
    }
    
        protected String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}
