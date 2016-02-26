/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.User;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 数据失效策略测试 (按设置时间失效)
 *
 * @author maping
 */
public class TestCase421Remote {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "case421.jdg.properties";

    public static void main(String[] args) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));

        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        RemoteCache<String, User> cache = cacheManager.getCache("case421_cache");

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

    public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(TestCase421Remote.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
