/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.google.protobuf.Descriptors;
import com.opencsv.CSVReader;
import com.redhat.poc.jdg.bankofchina.marshaller.UserBaseInfoMarshaller;
import com.redhat.poc.jdg.bankofchina.model.UserBaseInfo;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.util.Util;
import org.infinispan.protostream.SerializationContext;

/**
 * 数据加载测试 (client-server mode, 1个JDG缓存节点)
 * 读取/Users/maping/Code/cmcccachetest/data/test/data/userinfo/目录下csv文件
 * 每个文件启动一个线程,将数据Put到Cache中
 *
 * @author maping
 */
public class TestCase411RemoteMultiThreadsCustomMarshal extends Thread {

    private static final String CSV_FILE_PATH = "/Users/maping/Code/cmcccachetest/data/test/data/userinfo/";

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String REGISTER_PORT = "jdg.register.port";

    private static final String PROPERTIES_FILE = "case411.jdg.properties";
    private static final String CACHE_NAME = "case411_cache";
    private static int csvFileStart = 591;
    private static int csvFileEnd = 595;
    private int fileName;

    public TestCase411RemoteMultiThreadsCustomMarshal(int fileName) {
        this.fileName = fileName;
    }

    public static void main(String[] args) throws Exception {
        CommandLine commandLine;
        Options options = new Options();
        options.addOption("s", true, "The start csv file number option");
        options.addOption("e", true, "The end csv file number option");
        BasicParser parser = new BasicParser();
        parser.parse(options, args);
        commandLine = parser.parse(options, args);
        if (commandLine.getOptions().length > 0) {
            if (commandLine.hasOption("s")) {
                String start = commandLine.getOptionValue("s");
                if (start != null && start.length() > 0) {
                    csvFileStart = Integer.parseInt(start);
                }
            }
            if (commandLine.hasOption("e")) {
                String end = commandLine.getOptionValue("e");
                if (end != null && end.length() > 0) {
                    csvFileEnd = Integer.parseInt(end);
                }
            }
        }

        System.out.println("%%%%%%%%% 开始加载 csv 文件数据, 每个文件使用一个线程加载, 当前时间戳(ms)," + new Date().getTime());

        for (int i = csvFileStart; i <= csvFileEnd; i++) {
            new TestCase411RemoteMultiThreadsCustomMarshal(i).start();
        }
    }

    @Override
    public void run() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)))
                .marshaller(new ProtoStreamMarshaller());
        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());

        SerializationContext context = ProtoStreamMarshaller
                .getSerializationContext(cacheManager);
        try {
            context.registerProtofile("/model/userbaseinfo.protobin");
            context.registerMarshaller(new UserBaseInfoMarshaller());
            registerProtofile(jdgProperty(JDG_HOST), Integer.parseInt(jdgProperty(REGISTER_PORT)), "local");
        } catch (IOException e) {
            System.err.println("Could not register protobuf declarations");
            e.printStackTrace();
        } catch (Descriptors.DescriptorValidationException e) {
            System.err.println("Could not register protobuf declarations");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Problem registering protobuf file");
            e.printStackTrace();
        }

        final RemoteCache<String, UserBaseInfo> userinfoCache = cacheManager.getCache(CACHE_NAME);
        LoadUserBaseInfo(userinfoCache, fileName);
    }

    private static void LoadUserBaseInfo(BasicCache cache, int fileNumber) {
        try {
            //System.out.println("####### " + Thread.currentThread().getName() + " : 开始 把 " + fileNumber + ".csv 数据放入缓存. ");
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

            //int number = count - 1;
            //System.out.println("####### " + Thread.currentThread().getName() + " : " + fileNumber + ".csv 中记录数是 " + number);
            //System.out.println("####### " + Thread.currentThread().getName() + " : userinfo 缓存中的记录数是 " + cache.size());
            System.out.println("#######, " + Thread.currentThread().getName() + ", : 结束 把 " + fileNumber + ".csv 数据放入缓存, 共有 " + count + "条记录, 结束时间戳(ms), " + new Date().getTime());
        } catch (Exception ex) {
            Logger.getLogger(TestCase411RemoteMultiThreadsCustomMarshal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void registerProtofile(String jmxHost, int jmxPort,
            String cacheContainerName) throws Exception {
        JMXConnector jmxConnector = JMXConnectorFactory
                .connect(new JMXServiceURL("service:jmx:remoting-jmx://"
                                + jmxHost + ":" + jmxPort));
        MBeanServerConnection jmxConnection = jmxConnector
                .getMBeanServerConnection();

        ObjectName protobufMetadataManagerObjName = new ObjectName(
                "jboss.infinispan:type=RemoteQuery,name="
                + ObjectName.quote(cacheContainerName)
                + ",component=ProtobufMetadataManager");

        // initialize client-side serialization context via JMX
        byte[] descriptor = readClasspathResource("/model/userbaseinfo.protobin");
        jmxConnection.invoke(protobufMetadataManagerObjName,
                "registerProtofile", new Object[]{descriptor},
                new String[]{byte[].class.getName()});
        jmxConnector.close();
    }

    public static byte[] readClasspathResource(String c) throws IOException {
        InputStream is = TestCase411RemoteMultiThreadsCustomMarshal.class.getResourceAsStream(c);
        try {
            return Util.readStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(TestCase411RemoteMultiThreadsCustomMarshal.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
