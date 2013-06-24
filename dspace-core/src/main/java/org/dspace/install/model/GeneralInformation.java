package org.dspace.install.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GeneralInformation implements InstallObject {
	private String name;
	private URL url;
	private Language defaultLanguage;
	private List<Language> availableLanguages;
	
	public GeneralInformation () {
		this.availableLanguages = new ArrayList<Language>();
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}
	/**
	 * @return the defaultLanguage
	 */
	public Language getDefaultLanguage() {
		return defaultLanguage;
	}
	/**
	 * @param defaultLanguage the defaultLanguage to set
	 */
	public void setDefaultLanguage(Language defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	/**
	 * @return the availableLanguages
	 */
	public List<Language> getAvailableLanguages() {
		return availableLanguages;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object deepClone() {
		GeneralInformation instance = new GeneralInformation();
		instance.setDefaultLanguage(this.getDefaultLanguage());
		instance.setName(this.getName());
		try {
			instance.setUrl(new URL(this.getUrl().toString()));
		} catch (MalformedURLException e) {
			// Cannot copy URL
		}
		instance.getAvailableLanguages().addAll(this.getAvailableLanguages());
		return instance;
	}

}