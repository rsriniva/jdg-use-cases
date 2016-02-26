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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;

/**
 * 数据加载测试 (client-server mode, 1个JDG缓存节点)
 * 读取/opt/jbshome/data/userinfo/目录下csv文件
 * 并Put到Cache中
 *
 * @author maping
 */
public class TestCase411Remote {

    private static final String CSV_FILE_PATH = "/opt/jbshome/data/userinfo/";

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "case411.jdg.properties";
    private static final String CACHE_NAME = "case411_cache";
    private static long totalCsvRowNumber = 0;
    private static long totalLoadTime = 0;
    private static int csvFileStart = 591;
    private static int csvFileEnd = 595;
    private static DecimalFormat df = new DecimalFormat(",###");

    public static void main(String[] args) throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));

        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());

        final RemoteCache<String, UserBaseInfo> userinfoCache = cacheManager.getCache(CACHE_NAME);

        if (args[0] != null) {
            csvFileStart = Integer.parseInt(args[0]);
        }

        if (args[1] != null) {
            csvFileEnd = Integer.parseInt(args[1]);
        }

        for (int i = csvFileStart; i <= csvFileEnd; i++) {
            LoadUserBaseInfo(userinfoCache, i);
        }

        System.out.println();
        System.out.println("%%%%%%%%% 加载数据时间统计 %%%%%%%%%");
        System.out.println("%%%%%%%%% userinfo csv 文件记录总数是 " + df.format(totalCsvRowNumber));
        System.out.println("%%%%%%%%% userinfo 缓存中的记录数是 " + df.format(userinfoCache.size()));
        System.out.println("%%%%%%%%% 加载时间总耗时 (加载csv文件到内存时间 + 数据插入缓存时间)" + df.format(totalLoadTime) + " ms");
    }

    private static void LoadUserBaseInfo(BasicCache cache, int fileNumber) throws Exception {
        System.out.println("####### 开始 把 " + fileNumber + ".csv 数据放入缓存. ");
        long beginTime = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH + fileNumber + ".csv"));
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
        long loadTime = endTime - beginTime;
        totalLoadTime += loadTime;

        int number = count - 1;
        totalCsvRowNumber += number;
        System.out.println("####### " + fileNumber + ".csv 中记录数是 " + number);
        System.out.println("####### userinfo 缓存中的记录数是 " + cache.size());
        System.out.println("####### 结束 把 " + fileNumber + ".csv 数据放入缓存,耗时 " + loadTime + " ms");
    }

    public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(TestCase411Remote.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
