package com.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForgotController {
	@RequestMapping("/forgot")
	public String openEmail()
	{
		 
		return "forgot_email_form";
	}

}
