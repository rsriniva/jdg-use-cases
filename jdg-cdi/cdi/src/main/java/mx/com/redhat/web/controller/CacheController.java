package mx.com.redhat.web.controller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import mx.com.redhat.jdg.ejb.interfaces.GridService;

@Named
@RequestScoped
public class CacheController {
	
	@Inject
	GridService gridService;
	
	private String key;
	private String value;
	
	public void guardarObjeto(){
		System.out.println("Valor: " + this.value);
		System.out.println("Llave: " + this.key);
		gridService.saveValue(this.key, this.value);
	}
	
	public void obtenerValor(){
		System.out.println("Llave: " + this.key);
		this.setValue(gridService.getValue(this.key));
		System.out.println("Valor: " + this.value);
	}
	
	public void borrarValor(){
		gridService.deleteValue(this.key);
	}
	
	public int getCacheSize(){
		return gridService.getSize();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getCacheName(){
		return gridService.getName();
	}

}
