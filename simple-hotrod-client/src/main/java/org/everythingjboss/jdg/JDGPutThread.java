package org.everythingjboss.jdg;

import org.infinispan.client.hotrod.RemoteCache;

public class JDGPutThread implements Runnable {

	private RemoteCache<String,DummyClass> rc;
	private String key;
	private DummyClass value;
	
	public JDGPutThread(RemoteCache<String,DummyClass> rc, String key, DummyClass value) {
		this.rc = rc;
		this.key = key;
		this.value = value;
	}
	
	@Override
	public void run() {
		rc.put(key, value);
		System.out.println("Writing of key "+key+" is done!");
	}

}
