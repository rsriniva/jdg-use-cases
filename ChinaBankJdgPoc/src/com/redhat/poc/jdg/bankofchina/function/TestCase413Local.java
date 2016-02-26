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
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * 数据加载测试 （library mode, 1个缓存节点） 
 * 读取/opt/jbshome/data/userinfo/目录下csv文件
 * 并Put到Cache中
 *
 * @author maping
 */
public class TestCase413Local {

    private static final String CSV_FILE_PATH = "/opt/jbshome/data/userinfo/";
    private static boolean useXmlConfig = false;
    private static String cacheName = "repl";
    private static long totalCsvRowNumber = 0;
    private static long totalLoadTime = 0;
    private static DecimalFormat df = new DecimalFormat(",###");

    public TestCase413Local(boolean useXmlConfig, String cacheName) {
        TestCase413Local.useXmlConfig = useXmlConfig;
        TestCase413Local.cacheName = cacheName;
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
        new TestCase413Local(useXmlConfig, cacheName).run();
    }

    public void run() throws Exception {
        EmbeddedCacheManager cacheManager = createCacheManager();

        final Cache<String, UserBaseInfo> userinfoCache = cacheManager.getCache(cacheName);
        
        for (int i = 591; i < 596; i++) {
            LoadUserBaseInfo(userinfoCache, i);
        }

        System.out.println();
        System.out.println("%%%%%%%%% 加载数据时间统计 %%%%%%%%%");
        System.out.println("%%%%%%%%% userinfo csv 文件记录总数是 " + df.format(totalCsvRowNumber));
        System.out.println("%%%%%%%%% userinfo 缓存中的记录数是 " + df.format(userinfoCache.size()));
        System.out.println("%%%%%%%%% 加载时间总耗时 (加载csv文件到内存时间 + 数据插入缓存时间)" + df.format(totalLoadTime) + " ms");
    }

    private void LoadUserBaseInfo(Cache cache, int fileNumber) throws Exception {
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
        EmbeddedCacheManager cacheManager = new DefaultCacheManager(
                builder.build(),
                cacheBuider.build());

        return cacheManager;
    }

    private EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
        System.out.println("####### Starting a cache manager with an XML configuration");
        return new DefaultCacheManager("case411.xml");
    }
}
