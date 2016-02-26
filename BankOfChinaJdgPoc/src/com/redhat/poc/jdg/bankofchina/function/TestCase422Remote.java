/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.User;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * 数据失效策略测试 (按设置大小失效)
 *
 * @author maping
 */
public class TestCase422Remote {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "case422.jdg.properties";

    public static void main(String[] args) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));

        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        RemoteCache<String, User> cache = cacheManager.getCache("case422_cache");

        System.out.println("####### 开始放入 100 条 user 数据放到缓存中. " + new Date().toString());
        for (int i = 1; i <= 100; i++) {
            cache.put("user_" + i, new User());
        }

        System.out.println("####### 缓存大小是 " + cache.size());

//        while (cache.size() != 100) {
//            try {
//                sleep(10000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(TestCase422Remote.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        System.out.println("####### 缓存大小是 " + cache.size());

        System.out.println("####### 再放入 100 条 user 数据放到缓存中. " + new Date().toString());
        for (int i = 101; i <= 200; i++) {
            cache.put("user_" + i, new User());
        }

        System.out.println("####### 缓存大小是 " + cache.size());
    }

    public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(TestCase422Remote.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
