package org.dspace.springui.web.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.services.api.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.lyncode.jtwig.api.AssetResolver;

public class InstallationInterceptor implements HandlerInterceptor {
	private static final String INSTALL_REQUEST = "/install";
	private List<String> exceptions = new ArrayList<String>();
	@Autowired ConfigurationService configurationService;
	@Autowired AssetResolver assets;
	
	public void setExceptions (String[] exp) {
		this.exceptions = Arrays.asList(exp);
	}
	
	private boolean isException (String requestPath) {
		// if public
		String assetPrefix = assets.getPath("/");
		if (requestPath.startsWith(assetPrefix)) return true;
		else if (isInstall(requestPath)) return true;
		else {
			for (String exp : this.exceptions) {
				if (requestPath.matches(exp))
					return true;
			}
		}
		return false;
	}
	
	private boolean isInstall (String requestPath) {
		if (requestPath.startsWith(INSTALL_REQUEST)) return true;
		return false;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (!configurationService.isInstalled() && !this.isException(request.getPathInfo())) {
			response.sendRedirect(request.getContextPath() + INSTALL_REQUEST);
			return false;
		}
		if (configurationService.isInstalled() && this.isInstall(request.getPathInfo())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// Nothing
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// Nothing
	}

}
