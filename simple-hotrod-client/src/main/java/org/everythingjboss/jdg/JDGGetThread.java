package org.everythingjboss.jdg;

import org.infinispan.client.hotrod.RemoteCache;

public class JDGGetThread implements Runnable {

	private RemoteCache<String,DummyClass> rc;
	private String key;
	
	public JDGGetThread(RemoteCache<String,DummyClass> rc, String key) {
		this.rc = rc;
		this.key = key;
	}
	
	@Override
	public void run() {
		DummyClass dummyObj = rc.get(key);
		
		if(dummyObj != null)
			System.out.println("Successfully retrieved the <K,V> pair for the key :"+key);
	}

}
