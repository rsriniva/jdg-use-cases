/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.Person;
import com.redhat.poc.jdg.bankofchina.model.PersonDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.util.Util;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.redhat.poc.jdg.bankofchina.marshaller.PersonMarshaller;
import java.util.Properties;

/**
 * 缓存数据查询测试
 *
 * @author maping
 */
import static org.junit.Assert.*;

public class TestCase46Remote {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String REGISTER_PORT = "jdg.register.port";

    private static final String PROPERTIES_FILE = "case46.jdg.properties";
    private static final String CACHE_NAME = "case46_cache";

    public static void main(String[] args) {

        RemoteCache<Integer, Person> cache = getCache();
        if (cache == null) {
            System.exit(1);
        }

        try {
            // Clear cache
            cache.clear();
            // Load the cache with people
            List<Person> people = new PersonDatabase().getPeople();
            Random random = new Random(System.currentTimeMillis());
            for (Person person : people) {
                cache.put(random.nextInt(1000), person);
            }

            QueryFactory<Query> qf = Search.getQueryFactory(cache);

            // query for blondes
            Query query = qf.from(Person.class).having("hairColor")
                    .eq("blonde").toBuilder().build();

            List<Person> blondes = query.list();
            System.out.println("##### 查询 hairColor=blonde 的Person: \n" + blondes);

            query = qf.from(Person.class).having("age").gte(45).toBuilder()
                    .build();
            List<Person> adults = query.list();
            System.out.println("##### 查询 age>=45 的Person: \n" + adults);

            query = qf.from(Person.class)
                    .having("firstName").like("_a%")
                    .and()
                    .having("age").lt(40)
                    .toBuilder().build();

            List<Person> ms = query.list();
            System.out.println("##### 查询 firstNameage like '_a%' 并且 age<40 的Person: \n" + ms);

        } finally {
            cache.getRemoteCacheManager().stop();
        }

    }

    public static RemoteCache<Integer, Person> getCache() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)))
                .marshaller(new ProtoStreamMarshaller());

        RemoteCacheManager remoteManager = new RemoteCacheManager(
                builder.build());

        SerializationContext context = ProtoStreamMarshaller
                .getSerializationContext(remoteManager);
        try {
            context.registerProtofile("/model/person.protobin");
            context.registerMarshaller(new PersonMarshaller());
            registerProtofile(jdgProperty(JDG_HOST), Integer.parseInt(jdgProperty(REGISTER_PORT)), "local");
        } catch (IOException e) {
            System.err.println("Could not register protobuf declarations");
            e.printStackTrace();
            return null;
        } catch (DescriptorValidationException e) {
            System.err.println("Could not register protobuf declarations");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Problem registering protobuf file");
            e.printStackTrace();
            return null;
        }

        return remoteManager.getCache();
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
        byte[] descriptor = readClasspathResource("/model/person.protobin");
        jmxConnection.invoke(protobufMetadataManagerObjName,
                "registerProtofile", new Object[]{descriptor},
                new String[]{byte[].class.getName()});
        jmxConnector.close();
    }

    public static byte[] readClasspathResource(String c) throws IOException {
        InputStream is = TestCase46Remote.class.getResourceAsStream(c);
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
            props.load(TestCase46Remote.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
}
