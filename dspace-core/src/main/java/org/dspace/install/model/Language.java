package org.dspace.install.model;

public enum Language {
	PT("Portuguese (Portugal)", "pt-PT"),
	PT_BR("Portuguese (Brazil)", "pt-BR"),
	EN_US("English (US)", "en-US"),
	EN_UK("English (UK)", "en-UK");
	
	public static Language fromCode (String code) {
		for (Language g : Language.values()) {
			if (g.getCode().equals(code))
				return g;
		}
		return EN_US;
	}
	
	private String label;
	private String code;
	
	Language (String label, String code) {
		this.label = label;
		this.code = code;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
