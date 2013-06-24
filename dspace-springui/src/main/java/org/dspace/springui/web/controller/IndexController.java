package org.dspace.springui.web.controller;

import org.dspace.services.api.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/")
public class IndexController {
	@Autowired ConfigurationService configurationService;
	
	@RequestMapping(value="/")
	public String indexAction () {
		return "guest/index";
	}
}
