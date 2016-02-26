package org.everythingjboss.jdg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SecurityConfigurationBuilder;

public class SimpleHotRodClient {

	public static void main(String[] args) throws InterruptedException {

		RemoteCacheManager rcm;
		
		Boolean secured = (System.getProperty("secured")==null)?false:true;
		String serverEndpoint = (System.getProperty("serverEndpoint")==null)?"127.0.0.1:11222":System.getProperty("serverEndpoint");
		String cacheName = System.getProperty("cacheName")==null?"namedCache":System.getProperty("cacheName");
		
		if(secured) {
			SecurityConfigurationBuilder cb = new ConfigurationBuilder()
			.addServers(serverEndpoint)
			.security();
			cb.authentication()
			.enable()
			.serverName("myhotrodserver")
			.saslMechanism("DIGEST-MD5")
			.callbackHandler(new DefaultCallbackHandler("username", "password".toCharArray(), "ApplicationRealm"));
			cb.ssl()
					.enable()
					.keyStoreFileName("/Users/vchintal/jdg-client.keystore")
					.keyStorePassword("jdg-client^Pas$".toCharArray())
					.trustStoreFileName("/Users/vchintal/jdg-client.truststore")
			        .trustStorePassword("jdg-client-trust^Pas$".toCharArray());
			rcm = new RemoteCacheManager(cb.build());
		} else {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder
				.addServers(serverEndpoint)
				.connectionPool().maxTotal(1);
			rcm = new RemoteCacheManager(builder.build());
		}
		
		RemoteCache<String,DummyClass> cache = rcm.getCache(cacheName);
		SimpleHotRodClient shrc = new SimpleHotRodClient();
		shrc.writeEntries(cache);
		shrc.readEntries(cache);
		rcm.stop();
	}

	public void writeEntries(RemoteCache<String,DummyClass> rc) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(99);
		for(int i=0;i<100;i++) {
			executor.execute(new JDGPutThread(rc, Integer.toString(i), new DummyClass()));
		}
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);
	}
	
	public void readEntries(RemoteCache<String,DummyClass> rc) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(99);
		for(int i=0;i<100;i++) {
			executor.execute(new JDGGetThread(rc, Integer.toString(i)));
		}
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);
		
	}
}