package org.dspace.springui.web.controller.rest;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.dspace.install.model.InstallObject;
import org.dspace.install.step.AbstractStep;
import org.dspace.services.impl.application.DSpaceInstallService;
import org.dspace.springui.web.controller.InstallController;
import org.dspace.springui.web.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/server")
public class StatusServerController {
	private static Logger log = Logger.getLogger(StatusServerController.class);
	@Autowired DSpaceInstallService installService;
	@Autowired InstallController installController;
	
	@RequestMapping(value="/status")
	public @ResponseBody RestResult<Boolean> statusAction () {
		return new RestResult<Boolean>(true);
	}
	
	@RequestMapping(value="/install")
	public @ResponseBody RestResult<Boolean> installAction (HttpSession session, HttpServletResponse response) {
		try {
			List<InstallObject> objects = new ArrayList<InstallObject>();
			for (AbstractStep step : this.installController.getInstallationSteps())
				objects.add((InstallObject) session.getAttribute(installController.getSessionAttributeName(step)));
			installService.install(this.installController.getInstallationSteps(), objects);
			RestResult<Boolean> result = new RestResult<Boolean>(true);
			
			// Sending result
			MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
			converter.write(result, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
			response.getOutputStream().flush();
			response.getOutputStream().close();
			// Restarting the server
			installService.doFinal();
			return result;
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			return new RestResult<Boolean>(e.getMessage());
		}
	}
}
