package org.dspace.install.step;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.dspace.install.model.GeneralInformation;
import org.dspace.install.model.Language;
import org.dspace.services.api.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;


import static org.dspace.services.api.configuration.reference.PropertyReference.*;

public class GeneralStep extends AbstractStep {
	@Autowired ConfigurationService config;

	@Override
	public GeneralInformation validate (Map<String, Object> request) throws InstallException {
		String name = (String) request.get("name");
		String defaulLang = (String) request.get("default-language");
		String[] availableLanguages = (String[]) request.get("available-languages");
		if (availableLanguages == null) availableLanguages = new String[0];
		String url = (String) request.get("url");
		
		GeneralInformation general = new GeneralInformation();
		general.setName(name);
		general.setDefaultLanguage(Language.fromCode(defaulLang));
		for (String lang : availableLanguages)
			general.getAvailableLanguages().add(Language.fromCode(lang));
		if (!general.getAvailableLanguages().contains(general.getDefaultLanguage()))
			general.getAvailableLanguages().add(general.getDefaultLanguage());
		
		try {
			URL turl = new URL(url);
			general.setUrl(turl);
		} catch (MalformedURLException e) {
			throw new InstallException("Malformed URL",e);
		}
		return general;
	}

	@Override
	public void install(Object values) throws InstallException {
		GeneralInformation info = (GeneralInformation) values;
		String[] langs = new String[info.getAvailableLanguages().size()];
		for (int i = 0;i<info.getAvailableLanguages().size();i++)
			langs[i] = info.getAvailableLanguages().get(i).getCode();
		
		config.addProperty(key("dspace.name"), info.getName());
		config.addProperty(key("dspace.url"), info.getUrl().toString());
		config.addProperty(key("dspace.lang.default"), info.getDefaultLanguage().getCode());
		config.addProperty(key("dspace.lang.available"), langs);
	}

	@Override
	public void prepare(Map<String, Object> model) {
		model.put("possibleLanguages", Language.values());
	}


}
