/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.Data1M;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import static org.junit.Assert.assertEquals;

/**
 * 数据插入接口测试 (同步)
 *
 * @author maping
 */
public class TestCase431Remote {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "case431.jdg.properties";
    private static final int COUNT = 1000;

    public static void main(String[] args) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));

        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        RemoteCache<String, Data1M> cache = cacheManager.getCache("case431_cache");

        cache.clear();
        assertEquals(0, cache.size());
        putSync(cache);
    }

    public static void putSync(BasicCache<String, Data1M> cache) {
        System.out.println("####### 开始 插入 " + COUNT + " 1M 数据到缓存中 (同步) ");
        long beginTime = new Date().getTime();

        for (int i = 1; i <= COUNT; i++) {
            cache.put("data_sync_put" + i, new Data1M());
        }
        long endTime = new Date().getTime();
        long loadTime = endTime - beginTime;
        assertEquals(COUNT, cache.size());

        System.out.println("####### 结束 插入 " + COUNT + " 1M 数据到缓存中 (同步) , 耗时 " + loadTime + " ms");
    }

    public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(TestCase431Remote.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
