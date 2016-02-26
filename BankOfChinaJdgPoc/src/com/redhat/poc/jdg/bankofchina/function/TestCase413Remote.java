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
import org.infinispan.client.hotrod.impl.transport.tcp.RoundRobinBalancingStrategy;
import org.infinispan.commons.api.BasicCache;

/**
 * 数据加载测试 (client-server mode, 2个JDG缓存节点,分布式模式)
 * 读取/Users/maping/Code/cmcccachetest/data/test/data/userinfo/目录下csv文件
 * 并Put到Cache中
 *
 * @author maping
 */
public class TestCase413Remote {

    private static final String CSV_FILE_PATH = "/Users/maping/Code/cmcccachetest/data/test/data/userinfo/";

    private static final String JDG_SERVERS = "jdg.servers";
    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "case413.jdg.properties";
    private static final String CACHE_NAME = "case413_cache";
    private static long totalCsvRowNumber = 0;
    private static long totalLoadTime = 0;
    private static DecimalFormat df = new DecimalFormat(",###");

    public static void main(String[] args) throws Exception {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServers(jdgProperty(JDG_SERVERS));
        builder.pingOnStartup(true);
        builder.balancingStrategy(RoundRobinBalancingStrategy.class);

        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());

        final RemoteCache<String, UserBaseInfo> userinfoCache = cacheManager.getCache(CACHE_NAME);

        for (int i = 591; i < 595; i++) {
            LoadUserBaseInfo(userinfoCache, i);
        }

        System.out.println();
        System.out.println("%%%%%%%%% 加载数据时间统计 %%%%%%%%%");
        System.out.println("%%%%%%%%% userinfo csv 文件记录总数是 " + df.format(totalCsvRowNumber));
        System.out.println("%%%%%%%%% userinfo 缓存中的记录数是 " + df.format(userinfoCache.size()));
        System.out.println("%%%%%%%%% 加载时间总耗时 " + df.format(totalLoadTime) + " ms");
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
            props.load(TestCase413Remote.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
