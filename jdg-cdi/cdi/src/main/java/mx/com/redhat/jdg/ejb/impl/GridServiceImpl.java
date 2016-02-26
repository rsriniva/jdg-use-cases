package mx.com.redhat.jdg.ejb.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.cdi.Remote;
import org.infinispan.client.hotrod.RemoteCache;

import mx.com.redhat.jdg.ejb.interfaces.GridService;

@Stateless
public class GridServiceImpl implements GridService {

	@Inject
	@Remote("teams")
	private RemoteCache<Object, Object> cache;

	@Override
	public void saveValue(String key, String value) {
		// TODO Auto-generated method stub
		cache.put(key, value);

	}

	@Override
	public void deleteValue(String key) {
		// TODO Auto-generated method stub
		cache.remove(key);
	}

	@Override
	public String getValue(String key) {
		// TODO Auto-generated method stub
		if(cache.containsKey(key)){
			System.out.println("Si existe la llave");
			return String.valueOf(cache.get(key));
		}
		return null;
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		cache.clear();
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return cache.size();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return cache.getName();
	}

}
