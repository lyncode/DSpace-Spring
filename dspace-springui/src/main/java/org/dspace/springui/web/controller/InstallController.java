package org.dspace.springui.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.dspace.install.step.AbstractStep;
import org.dspace.install.step.DatabaseStep;
import org.dspace.install.step.EmailServerStep;
import org.dspace.install.step.GeneralStep;
import org.dspace.install.step.InstallException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/install")
public class InstallController {
	private static Logger log = Logger.getLogger(InstallController.class);
	
	private List<AbstractStep> steps;
	
	public InstallController () {
		steps = new ArrayList<AbstractStep>();
		steps.add(new GeneralStep());
		steps.add(new DatabaseStep());
		steps.add(new EmailServerStep());
	}
	
	public List<AbstractStep> getInstallationSteps () {
		return this.steps;
	}
	
	@RequestMapping("")
	public String indexAction (ModelMap model) {
		AbstractStep step = steps.get(0);
		model.addAttribute("currentStep", 1);
		model.addAttribute("nextStep", 2);
		step.prepare(model);
		return "install/step/"+this.getView(step);
	}

	

	@RequestMapping(value="/step/{id}", method = RequestMethod.POST)
	public String stepAction (ModelMap model, HttpSession session, HttpServletRequest request,
			@PathVariable("id") int stepId) {
		boolean isFinal = false;
		model.addAttribute("request", request);
		AbstractStep previousStep = steps.get(stepId-2);
		AbstractStep nextStep = null;
		if (stepId <= steps.size()) 
			nextStep = steps.get(stepId-1);
		else
			isFinal = true;
		try {
			Object store = previousStep.validateParameters(request.getParameterMap());
			session.setAttribute(this.getSessionAttributeName(previousStep), store);
			if (isFinal) return "forward:/install/final";
		} catch (InstallException e) {
			log.error(e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			model.addAttribute("currentStep", stepId-1);
			model.addAttribute("nextStep", stepId);
			previousStep.prepare(model);
			return "install/step/"+this.getView(previousStep);
		}
		model.addAttribute("currentStep", stepId);
		model.addAttribute("nextStep", stepId+1);
		nextStep.prepare(model);
		return "install/step/"+this.getView(nextStep);
	}
	

	@RequestMapping(value="/final")
	public String finalAction (ModelMap model, HttpServletRequest request) {
		model.addAttribute("currentStep", this.steps.size());
		return "install/step/final";
	}
	
	public String getSessionAttributeName (AbstractStep step) {
		return "install.step."+step.getClass().getSimpleName();
	}
	
	private String getView (AbstractStep step) {
		return step.getClass().getSimpleName();
	}
}
