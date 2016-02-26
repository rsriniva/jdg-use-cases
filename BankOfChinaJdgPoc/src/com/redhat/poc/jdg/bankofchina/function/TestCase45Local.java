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
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * 缓存数据持久化测试
 *
 * @author maping
 */
public class TestCase45Local {

    private static String csvFilePath = "/Users/maping/Code/cmcccachetest/data/test/data/userinfo/";
    private static boolean useXmlConfig = false;
    private static String cacheName = "repl";

    public TestCase45Local(boolean useXmlConfig, String cacheName) {
        TestCase45Local.useXmlConfig = useXmlConfig;
        TestCase45Local.cacheName = cacheName;
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
        new TestCase45Local(useXmlConfig, cacheName).run();
    }

    public void run() throws Exception {
        EmbeddedCacheManager cacheManager = createCacheManager();

        final Cache<String, UserBaseInfo> userinfoCache = cacheManager.getCache(cacheName);
        LoadUserBaseInfo(userinfoCache);

    }

    private void LoadUserBaseInfo(Cache cache) throws Exception {
        System.out.println("####### 开始 把 591.csv 数据放入缓存. ");
        long beginTime = new Date().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        CSVReader reader = new CSVReader(new FileReader(csvFilePath + "591.csv"));
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

        ConfigurationBuilder cacheBuilder = new ConfigurationBuilder();
        cacheBuilder.persistence()
                .passivation(false)
                .addSingleFileStore()
                .location("/tmp/case45LocalFileStore")
                .fetchPersistentState(true)
                .ignoreModifications(false)
                .purgeOnStartup(false)
                .shared(false)
                .preload(false)
                .async()
                .enable()
                .threadPoolSize(500)
                .flushLockTimeout(1)
                .modificationQueueSize(25000);

         EmbeddedCacheManager cacheManager = new DefaultCacheManager(
                builder.build(),
                cacheBuilder.build());

        return cacheManager;
    }

    private EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
        System.out.println("####### Starting a cache manager with an XML configuration");
        return new DefaultCacheManager("case45.xml");
    }
}
