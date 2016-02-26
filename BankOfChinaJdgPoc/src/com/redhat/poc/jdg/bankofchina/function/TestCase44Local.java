/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.util.LoggingListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;

/**
 * 事件通知机制支持
 *
 * @author maping
 */
public class TestCase44Local {

    private static final BasicLogger log = Logger.getLogger(TestCase44Local.class);

    private static boolean useXmlConfig = false;
    private static String cacheName = "repl";
    private static String nodeName;
    private volatile boolean stop = false;

    public TestCase44Local(boolean useXmlConfig, String cacheName, String nodeName) {
        TestCase44Local.useXmlConfig = useXmlConfig;
        TestCase44Local.cacheName = cacheName;
        TestCase44Local.nodeName = nodeName;
    }

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            if ("-x".equals(arg)) {
                useXmlConfig = true;
            } else if ("-p".equals(arg)) {
                useXmlConfig = false;
            } else if ("-d".equals(arg)) {
                cacheName = "dist";
            } else if ("-r".equals(arg)) {
                cacheName = "repl";
            } else {
                nodeName = arg;
            }
        }
        new TestCase44Local(useXmlConfig, cacheName, nodeName).run();
    }

    public void run() throws IOException, InterruptedException {
        EmbeddedCacheManager cacheManager = createCacheManager();
        final Cache<String, String> cache = cacheManager.getCache(cacheName);
        System.out.printf("缓存 %s 启动 %s, 缓存成员有 %s\n", cacheName, cacheManager.getAddress(),
                cache.getAdvancedCache().getRpcManager().getMembers());

        // Add a listener so that we can see the puts to this node
        cache.addListener(new LoggingListener());

        Thread putThread = new Thread() {
            @Override
            public void run() {
                int counter = 0;
                while (!stop) {
                    try {
                        cache.put("key-" + counter, "" + counter);
                        cache.put("key-" + counter, "" + cache.getAdvancedCache().getRpcManager().getAddress() + "-" + counter);
                        cache.remove("key-" + counter);
                    } catch (Exception e) {
                        log.warnf("Error inserting key into the cache", e);
                    }
                    counter++;

                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        putThread.start();

        System.out.println("Press Enter to print the cache contents, Ctrl+D/Ctrl+Z to stop.");
        while (System.in.read() > 0) {
            printCacheContents(cache);
        }

        stop = true;
        putThread.join();
        cacheManager.stop();
        System.exit(0);
    }

    /**
     * {@link org.infinispan.Cache#entrySet()}
     *
     * @param cache
     */
    private void printCacheContents(Cache<String, String> cache) {
        System.out.printf("Cache contents on node %s\n", cache.getAdvancedCache().getRpcManager().getAddress());

        ArrayList<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(cache.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        for (Map.Entry<String, String> e : entries) {
            System.out.printf("\t%s = %s\n", e.getKey(), e.getValue());
        }
        System.out.println();
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

        DefaultCacheManager cacheManager = new DefaultCacheManager(
                GlobalConfigurationBuilder.defaultClusteredBuilder()
                .transport().nodeName(nodeName).addProperty("configurationFile", "udp.xml")
                .build(),
                new ConfigurationBuilder()
                .clustering()
                .cacheMode(CacheMode.REPL_SYNC)
                .build()
        );

        return cacheManager;
    }

    private EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
        System.out.println("####### Starting a cache manager with an XML configuration");
        System.setProperty("nodeName", nodeName);
        return new DefaultCacheManager("case44.xml");
    }
}
