package org.dspace.services.api.configuration.reference;

/**
 * Property reference
 * 
 * @author jsmelo
 *
 */
public class PropertyReference {
	public static final String DEFAULT_MODULE = "dspace";
	public static final PropertyReference INSTALLED = PropertyReference.key("dspace.installed");
	
	public static PropertyReference key (Module module, String key) {
		return new PropertyReference(module.name().toLowerCase(), key);
	}
	public static PropertyReference key (String module, String key) {
		return new PropertyReference(module, key);
	}
	public static PropertyReference key (String key) {
		return new PropertyReference(key);
	}
	
	
	private String module;
	private String key;
	
	private PropertyReference (String key) {
		this.module = null;
		this.key = key;
	}

	private PropertyReference (String module, String key) {
		this.module = module;
		this.key = key;
	}
	/**
	 * @return the module
	 */
	public String getModule() {
		return (module != null) ? module : DEFAULT_MODULE;
	}
	
	public boolean isDefault () {
		return module == null;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
}
