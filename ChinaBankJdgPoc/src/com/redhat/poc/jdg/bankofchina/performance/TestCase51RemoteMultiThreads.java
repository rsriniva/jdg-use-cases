/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.performance;

import com.redhat.poc.jdg.bankofchina.function.*;
import com.opencsv.CSVReader;
import com.redhat.poc.jdg.bankofchina.model.UserBaseInfo;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * 随机读测试 (client-server mode, 1个JDG缓存节点)
 *
 * @author maping
 */
public class TestCase51RemoteMultiThreads extends Thread {

    private static final String CSV_FILE_PATH = "/opt/jbshome/data/userinfo/";

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "case411.jdg.properties";
    private static final String CACHE_NAME = "case411_cache";
    private static DecimalFormat df = new DecimalFormat(",###");
    private static int readThreadNumbers = 50;
    private static int threadReadTimes = 40000;
    private static List<String> userIdList = new ArrayList<String>();
    private static int randomPoolSize;
    private static int randomPoolSizeByThread;
    private int randomStartIndexByThread;

    public TestCase51RemoteMultiThreads(int randomStartIndex) {
        this.randomStartIndexByThread = randomStartIndex;
    }

    public static void main(String[] args) throws Exception {
        CommandLine commandLine;
        Options options = new Options();
        options.addOption("n", true, "The read thread numbers option");
        options.addOption("t", true, "The thread read times option");
        BasicParser parser = new BasicParser();
        parser.parse(options, args);
        commandLine = parser.parse(options, args);
        if (commandLine.getOptions().length > 0) {
            if (commandLine.hasOption("n")) {
                String numbers = commandLine.getOptionValue("n");
                if (numbers != null && numbers.length() > 0) {
                    readThreadNumbers = Integer.parseInt(numbers);
                }
            }
            if (commandLine.hasOption("t")) {
                String times = commandLine.getOptionValue("t");
                if (times != null && times.length() > 0) {
                    threadReadTimes = Integer.parseInt(times);
                }
            }
        }
        
        System.out.println();
        System.out.println("%%%%%%%%% 开始加载 userid.csv 到內存 %%%%%%%%%");
        LoadUserIdList();
        randomPoolSize = userIdList.size();
        System.out.println("%%%%%%%%% 结束加载 userid.csv 到內存, 一共 " + randomPoolSize + " 条数据 %%%%%%%%%");
        randomPoolSizeByThread = randomPoolSize / readThreadNumbers;

        System.out.println();
        System.out.println("%%%%%%%%% 开始启动, " + readThreadNumbers + " ,个读线程, 每个线程随机读取, " + threadReadTimes + " ,条记录, 当前时间戳(ms)," + new Date().getTime());

        for (int i = 0; i < readThreadNumbers; i++) {
            new TestCase51RemoteMultiThreads(i * randomPoolSizeByThread).start();
        }
    }

    @Override
    public void run() {
        RandomRead(randomStartIndexByThread);
//        System.out.println("%%%%%%%%% 读线程 " + Thread.currentThread().getName() + " 随机开始基数: " + randomStartIndexByThread);
    }

    private static void RandomRead(int randomStart) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));
        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        final RemoteCache<String, UserBaseInfo> userinfoCache = cacheManager.getCache(CACHE_NAME);

        for (int i = 0; i < threadReadTimes; i++) {
            // random read
            int rand = new Random().nextInt(randomPoolSizeByThread) + randomStart;
            String randomUserId = userIdList.get(rand);
            UserBaseInfo user = userinfoCache.get(randomUserId);
//            System.out.println("####### 读线程 " + Thread.currentThread().getName() + " 随机读第 " + (rand + 1) + " 条数据, "
//                    + " 其内容是: \n" + userinfoCache.get(randomUserId));
        }
        System.out.println("%%%%%%%%% 读线程 " + Thread.currentThread().getName() + " 结束时间戳(ms), " + new Date().getTime() + ", %%%%%%%%% ");
    }

    private static void LoadUserIdList() throws Exception {
        CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH + "userid.csv"));
        String[] nextLine;
        int count = 0;
        while ((nextLine = reader.readNext()) != null) {
            if (count == 0) {
            } else {
                // nextLine[] is an array of values from the line
                String userId = nextLine[0];
                userIdList.add(userId);
            }
            count++;
        }
    }

    public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(TestCase51RemoteMultiThreads.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
