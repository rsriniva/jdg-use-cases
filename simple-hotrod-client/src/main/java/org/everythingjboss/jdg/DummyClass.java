package org.everythingjboss.jdg;

import java.io.Serializable;

public class DummyClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -235277260571551968L;
	private byte [] blob;
	
	public DummyClass() {
		int bytes = System.getProperty("bytes")==null?500:Integer.parseInt(System.getProperty("bytes"));
		this.blob = new byte[bytes];
	}
}
