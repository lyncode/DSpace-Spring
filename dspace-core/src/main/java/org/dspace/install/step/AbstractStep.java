package org.dspace.install.step;

import java.util.HashMap;
import java.util.Map;
import org.dspace.install.model.InstallObject;

public abstract class AbstractStep {
	public abstract void prepare (Map<String, Object> presetValues);
	public abstract InstallObject validate (Map<String, Object> parameters) throws InstallException;
	public abstract void install (Object object) throws InstallException;
	
	public InstallObject validateParameters (Map<String, String[]> requestParameters) throws InstallException {
		Map<String, Object> objects = new HashMap<String, Object>();
		for (String key : requestParameters.keySet()) {
			if (requestParameters.get(key) != null) {
				String[] objs = requestParameters.get(key);
				if (objs.length == 1) objects.put(key, objs[0]);
				else objects.put(key, objs);
			}
		}
		return this.validate(objects);
	}
}
