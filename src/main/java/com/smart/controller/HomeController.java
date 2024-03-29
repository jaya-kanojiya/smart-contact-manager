package com.smart.controller;

//import javax.naming.Binding;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.smart.User;
import com.smart.Dao.UserRepository;
import com.smart.helper.Message;

//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;

//import ch.qos.logback.core.model.Model;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@RequestMapping("/")
	public String home(Model model)
	{
		
		model.addAttribute("title","Home - Smart contact manager");
		return "home";
	
     }
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","About - Smart contact manager");
		return "about";
    }
	@RequestMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title","Sign up - Smart contact manager");
		model.addAttribute("user",new User());
		return "signup";
    }
	//handler for registering user
	@RequestMapping(value="/do_register", method=RequestMethod.POST)
	public String registerUser(@Valid@ModelAttribute("user") User user,BindingResult result1,

			@RequestParam(value="agreement", 
	defaultValue="false") boolean agreement, Model model, HttpSession session)
	//handler for user login
	
	
	
	{
		try {
			
			if(!agreement)
		{
			System.out.println("Are you sure...you don't agree terms and conditions");
				throw new Exception("Are you sure...you don't agree terms and conditions");
			}
			if(result1.hasErrors())
			{
				System.out.println("ERROR " + result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("Agreement " + agreement);
			System.out.println("User " + user);
			
			User result = this.userRepository.save(user);
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully registered!!", "alert-success"));
			return "signup";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message" , new Message("Something went wrong!!" + e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
    }

    //Handler for user login
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}
}
