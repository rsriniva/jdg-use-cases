/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.User;
import java.io.IOException;
import java.util.Date;
import javax.transaction.TransactionManager;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import static org.junit.Assert.assertEquals;

/**
 * 事务完整性和数据一致性测试
 *
 * @author maping
 */
public class TestCase47Local {

    private static boolean useXmlConfig = false;
    private static String cacheName = "repl";

    public TestCase47Local(boolean useXmlConfig, String cacheName) {
        TestCase47Local.useXmlConfig = useXmlConfig;
        TestCase47Local.cacheName = cacheName;
    }

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            switch (arg) {
                case "-x":
                    useXmlConfig = true;
                    break;
                case "-p":
                    useXmlConfig = false;
                    break;
                case "-d":
                    cacheName = "dist";
                    break;
                case "-r":
                    cacheName = "repl";
                    break;
                default:
                    break;
            }
        }
        new TestCase47Local(useXmlConfig, cacheName).run();
    }

    public void run() throws IOException, InterruptedException {
        EmbeddedCacheManager cacheManager = createCacheManager();
        final Cache<String, User> cache = cacheManager.getCache(cacheName);

        System.out.println("####### Test Update Case 1");
        updateCase1(cache);
        System.out.println("####### After Update Case 1, Cache Size: " + cache.size());
        assertEquals(0, cache.size());

        System.out.println("####### Test Update Case 2");
        updateCase2(cache);
        System.out.println("####### After Update Case 2, Cache Size: " + cache.size());
        assertEquals(0, cache.size());

        System.out.println("####### Test Update Case 3");
        updateCase3(cache);
        System.out.println("####### After Update Case 3, Cache Size: " + cache.size());
        assertEquals(2, cache.size());
    }

    public void updateCase1(Cache<String, User> cache) {
        TransactionManager tm = cache.getAdvancedCache().getTransactionManager();
        boolean throwInducedException = true;
        try {
            tm.begin();
            System.out.println("####### start putting jdoe1 user into cache. " + new Date().toString());
            cache.put("jdoe1", new User());
            System.out.println("####### start putting jdoe2 user into cache. " + new Date().toString());
            cache.put("jdoe2", new User());
            if (throwInducedException) {
                System.out.println("####### exception occured. " + new Date().toString());
                throw new RuntimeException("Induced exception");
            }
            tm.commit();
        } catch (Exception e) {
            if (tm != null) {
                try {
                    tm.rollback();
                } catch (Exception e1) {
                }
            }
        }
    }

    public void updateCase2(Cache<String, User> cache) {
        TransactionManager tm = cache.getAdvancedCache().getTransactionManager();
        boolean throwInducedException = true;
        try {
            tm.begin();
            System.out.println("####### start putting jdoe1 user into cache. " + new Date().toString());
            cache.put("jdoe1", new User());
            if (throwInducedException) {
                System.out.println("####### exception occured. " + new Date().toString());
                throw new RuntimeException("Induced exception");
            }
            System.out.println("####### start putting jdoe2 user into cache. " + new Date().toString());
            cache.put("jdoe2", new User());
            tm.commit();
        } catch (Exception e) {
            if (tm != null) {
                try {
                    tm.rollback();
                } catch (Exception e1) {
                }
            }
        }
    }

    public void updateCase3(Cache<String, User> cache) {
        TransactionManager tm = cache.getAdvancedCache().getTransactionManager();
        boolean throwInducedException = false;
        try {
            tm.begin();
            System.out.println("####### start putting jdoe1 user into cache. " + new Date().toString());
            cache.put("jdoe1", new User());
            System.out.println("####### start putting jdoe2 user into cache. " + new Date().toString());
            cache.put("jdoe2", new User());
            if (throwInducedException) {
                System.out.println("####### exception occured. " + new Date().toString());
                throw new RuntimeException("Induced exception");
            }
            tm.commit();
        } catch (Exception e) {
            if (tm != null) {
                try {
                    tm.rollback();
                } catch (Exception e1) {
                }
            }
        }
    }

    private EmbeddedCacheManager createCacheManager() throws IOException {
        if (useXmlConfig) {
            return createCacheManagerFromXml();
        } else {
            return createCacheManagerProgrammatically();
        }
    }

    private EmbeddedCacheManager createCacheManagerProgrammatically() {
        System.out.println("####### Starting a cache manager with a programmatic configuration");

        GlobalConfiguration glob = new GlobalConfigurationBuilder()
                .nonClusteredDefault() //Helper method that gets you a default constructed GlobalConfiguration, preconfigured for use in LOCAL mode
                .globalJmxStatistics().enable() //This method allows enables the jmx statistics of the global configuration.
                .jmxDomain("com.redhat.poc.jdg.chinamobile.case634") //prevent collision with non-transactional
                .build(); //Builds  the GlobalConfiguration object
        Configuration loc = new ConfigurationBuilder()
                .jmxStatistics().enable() //Enable JMX statistics
                .clustering().cacheMode(CacheMode.LOCAL) //Set Cache mode to LOCAL - Data is not replicated.
                .transaction().transactionMode(TransactionMode.TRANSACTIONAL).autoCommit(false) //Enable Transactional mode with autocommit false
                .lockingMode(LockingMode.OPTIMISTIC).transactionManagerLookup(new GenericTransactionManagerLookup()) //uses GenericTransactionManagerLookup - This is a lookup class that locate transaction managers in the most  popular Java EE application servers. If no transaction manager can be found, it defaults on the dummy transaction manager.
                .locking().isolationLevel(IsolationLevel.REPEATABLE_READ) //Sets the isolation level of locking
                .eviction().maxEntries(4).strategy(EvictionStrategy.LIRS) //Sets  4 as maximum number of entries in a cache instance and uses the LIRS strategy - an efficient low inter-reference recency set replacement policy to improve buffer cache performance
                .persistence().passivation(false).addSingleFileStore().purgeOnStartup(true) //Disable passivation and adds a SingleFileStore that is purged on Startup
                .build(); //Builds the Configuration object
        EmbeddedCacheManager cacheManager = new DefaultCacheManager(glob, loc, true);

        return cacheManager;
    }

    private EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
        System.out.println("####### Starting a cache manager with an XML configuration");
        return new DefaultCacheManager("case47.xml");
    }
}
