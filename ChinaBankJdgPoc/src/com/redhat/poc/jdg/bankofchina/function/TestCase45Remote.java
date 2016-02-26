/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.opencsv.CSVReader;
import com.redhat.poc.jdg.bankofchina.model.UserBaseInfo;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;

/**
 * 缓存数据持久化测试
 *
 * @author maping
 */
public class TestCase45Remote {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "case45.jdg.properties";
    private static final String CACHE_NAME = "case45_cache";
    private static final String CSV_FILE_PATH = "/opt/jbshome/data/userinfo/";

    public static void main(String[] args) throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));

        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());

        final RemoteCache<String, UserBaseInfo> userinfoCache = cacheManager.getCache(CACHE_NAME);
        LoadUserBaseInfo(userinfoCache);

    }

    private static void LoadUserBaseInfo(BasicCache cache) throws Exception {
        System.out.println("####### 开始 把 591.csv 数据放入缓存. ");
        long beginTime = new Date().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH + "591.csv"));
        String[] nextLine;
        int count = 0;
        while ((nextLine = reader.readNext()) != null) {
            if (count == 0) {
            } else {
                // nextLine[] is an array of values from the line
                String userId = nextLine[0];
                String imsi = nextLine[1];
                String msisdn = nextLine[2];
                String homeCity = nextLine[3];
                String homeCountry = nextLine[4];
                Date inureTime = sdf.parse(nextLine[5]);
                Date expireTime = sdf.parse(nextLine[6]);
                String testFlag = nextLine[7];
                cache.put(userId, new UserBaseInfo(userId, imsi, msisdn, homeCity, homeCountry, inureTime, expireTime, testFlag));
            }
            count++;
        }
        long endTime = new Date().getTime();
        System.out.println("####### 结束 591.csv 数据放入缓存,耗时 " + (endTime - beginTime) + " ms");

        int number = count - 1;
        System.out.println("####### 591.csv 中数据行数是 " + number);
        System.out.println("####### userinfo 缓存中数据大小是 " + cache.size());        
    }

    public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(TestCase45Remote.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
