/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.web.controller;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.rda.web.base.controller.BaseController;
import com.rda.web.model.LoginForm;


/**
 * <P>test shiro security framework</P>
 * @author lianrao
 */
@Controller
@RequiresAuthentication
public class ShiroController extends BaseController {

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	/*
	 * url路径使用{}包起来则spring mvc在解析路径时将分析此变量而不是简单的字符串匹配,<br/>
	 * 例如url "/login;jsessionid=xxxx"会被解析成/login + matrixVariable,<br/>
	 * 若不使用{}将login包起来则只是简单字符串匹配，上述url不会匹配到/login url.
	 */
	@RequestMapping(value = "/{login}",method = RequestMethod.GET)
	public String loginIndex(ModelMap model,@MatrixVariable(required=false) String JSESSIONID ) {
		model.addAttribute("loginForm", new LoginForm());
		return "login";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{login}")
	public String register(@ModelAttribute(value = "loginForm") @Valid LoginForm loginForm, BindingResult result,
			Model model) {
		log.trace("Entering Authenticate");

		if (result.hasErrors()) {

			return "login";
		}

		UsernamePasswordToken token = new UsernamePasswordToken(loginForm.getUsername(), loginForm.getPassphrase());

		// �Remember Me� built-in, just do this
		// TODO: Make this a user option instead of hard coded in.
		token.setRememberMe(true);

		Subject currentUser = SecurityUtils.getSubject();

		try {
			currentUser.login(token);
			log.info("AUTH SUCCESS");
		} catch (AuthenticationException ae) {
			log.info("AUTH MSSG: " + ae.getMessage());
		}

		if (currentUser.isAuthenticated()) {
			return "redirect:/home";
		}

		return "login";
	}

}
