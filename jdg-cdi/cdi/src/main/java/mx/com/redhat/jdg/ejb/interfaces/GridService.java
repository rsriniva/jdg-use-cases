package mx.com.redhat.jdg.ejb.interfaces;

import javax.ejb.Local;

@Local
public interface GridService {
	public void saveValue(String key,String value);
	public void deleteValue(String key);
	public String getValue(String key);
	public void clearCache();
	public int getSize();
	public String getName();
}
