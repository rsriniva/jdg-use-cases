/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.User;
import java.io.IOException;
import java.util.Date;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.eviction.EvictionThreadPolicy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * 数据失效策略测试 (按设置大小失效)
 *
 * @author maping
 */
public class TestCase422Local {

    private static final int cacheSize = 100;
    private static boolean useXmlConfig = false;
    private static String cacheName = "repl";

    public TestCase422Local(boolean useXmlConfig, String cacheName) {
        TestCase422Local.useXmlConfig = useXmlConfig;
        TestCase422Local.cacheName = cacheName;
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
        new TestCase422Local(useXmlConfig, cacheName).run();
    }

    public void run() throws IOException, InterruptedException {
        EmbeddedCacheManager cacheManager = createCacheManager();
        final Cache<String, User> cache = cacheManager.getCache(cacheName);

        System.out.println("####### 开始放入 100 条 user 数据放到缓存中. " + new Date().toString());
        for (int i = 1; i <= 100; i++) {
            cache.put("user_" + i, new User());
        }

//        try {
//            // sleep 30 sec
//            Thread.sleep(30000);
//        } catch (InterruptedException ex) {
//        }

        System.out.println("####### 缓存大小是 " + cache.size());

        System.out.println("####### 再放入 100 条 user 数据放到缓存中. " + new Date().toString());
        for (int i = 101; i <= 200; i++) {
            cache.put("user_" + i, new User());
        }

//        try {
//            // sleep 30 sec
//            Thread.sleep(30000);
//        } catch (InterruptedException ex) {
//        }

        System.out.println("####### 缓存大小是 " + cache.size());

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

        GlobalConfigurationBuilder builder = new GlobalConfigurationBuilder();
        builder.nonClusteredDefault();
        ConfigurationBuilder cacheBuider = new ConfigurationBuilder();
        // if cache size over 100, entry will be evicted.
        cacheBuider.eviction()
                .strategy(EvictionStrategy.LIRS)
                .maxEntries(TestCase422Local.cacheSize)
                .threadPolicy(EvictionThreadPolicy.PIGGYBACK);
        EmbeddedCacheManager cacheManager = new DefaultCacheManager(
                builder.build(),
                cacheBuider.build());

        return cacheManager;
    }

    private EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
        System.out.println("####### Starting a cache manager with an XML configuration");
        return new DefaultCacheManager("case422.xml");
    }
}
