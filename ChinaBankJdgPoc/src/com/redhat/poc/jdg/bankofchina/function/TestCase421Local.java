/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.User;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 数据失效策略测试 (按设置时间失效)
 *
 * @author maping
 */
public class TestCase421Local {

    private static final int lifespan = 60;
    private static boolean useXmlConfig = false;
    private static String cacheName = "repl";

    public TestCase421Local(boolean useXmlConfig, String cacheName) {
        TestCase421Local.useXmlConfig = useXmlConfig;
        TestCase421Local.cacheName = cacheName;
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
        new TestCase421Local(useXmlConfig, cacheName).run();
    }

    public void run() throws IOException, InterruptedException {
        EmbeddedCacheManager cacheManager = createCacheManager();
        final Cache<String, User> cache = cacheManager.getCache(cacheName);

        System.out.println("####### 开始把 jdoe user 放到缓存中. " + new Date().toString());
        cache.put("jdoe", new User());

        try {
            // sleep 30 sec
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
        }
        assertTrue(cache.containsKey("jdoe"));
        System.out.println("####### 30秒后, jdoe user 在缓存中. " + new Date().toString());

        try {
            // sleep 30 sec
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
        }
        assertFalse(cache.containsKey("jdoe"));
        System.out.println("####### 60秒后, jdoe user 不在缓存中. " + new Date().toString());
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
        // after 60 sec, entry will be expired.
        cacheBuider.expiration().lifespan(TestCase421Local.lifespan, TimeUnit.SECONDS);
        EmbeddedCacheManager cacheManager = new DefaultCacheManager(
                builder.build(),
                cacheBuider.build());

        return cacheManager;
    }

    private EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
        System.out.println("####### Starting a cache manager with an XML configuration");
        return new DefaultCacheManager("case421.xml");
    }
}
