package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Path;
//import javax.persistence.criteria.Path;
import javax.servlet.http.HttpSession;
//import javax.validation.Path;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.data.jpa.domain.JpaSort.Path;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
//import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Contact;
import com.smart.User;
import com.smart.Dao.ContactRepository;
import com.smart.Dao.UserRepository;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String userName = principal.getName();
		System.out.println("USERNAME " + userName);
		//get the user using username
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER " + user);
		model.addAttribute("user",user);
	}
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
		
	}
	//open and form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title", "Add Contacts");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	//processing at contact form
	@PostMapping("/process-contact")
	//@PostMapping(value="/process-contact",consumes="multipart/form-data")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file ,Principal principal)
			//,HttpSession session)
	{
		
		try {
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		//PROCESSION ON file
		if(file.isEmpty())
		{
			//file is empty
			System.out.println("image is empty");
			contact.setImage("/contact.png");
		}else
		{
			//file is present
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("/static/img").getFile();
			//java.nio.file.
			
			java.nio.file.Path path= Paths.get(saveFile.getAbsolutePath()+File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
		    System.out.println("image is uploaded");
			
		}
	
		user.getContacts().add(contact);
		contact.setUser(user);
		this.userRepository.save(user);
		
		System.out.println("DATA " + contact);
		System.out.println("User added to database");
		//success message
		//session.setAttribute("message", new Message("Your contact is added!! add more","success"));
		
		} catch(Exception e)
		{
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
			//error message
		//	session.setAttribute("message", new Message("Something went wrong!! try again","danger"));
			
		}
	
		return "normal/add_contact_form";
	}
	
	//Show contact handler
	//@ModelAttribute
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal)
	{
		m.addAttribute("title","Show user contacts");
		
		String username = principal.getName();
	
		User user =this.userRepository.getUserByUserName(username);

		Pageable pageable=PageRequest.of(page, 5);
	//	user.getContacts();
		Page <Contact> contacts =this.contactRepository.findContactByUser(user.getId(),pageable);
		m.addAttribute("contacts", contacts);
        m.addAttribute("currentpage",page);
        m.addAttribute("tatol pages",contacts.getTotalPages());
            return "normal/show_contacts";
	}
	@RequestMapping("/contact/{cid}")
	public String showContactDetails(@PathVariable("cid") Integer cid,Model model,Principal principal)
	{
		System.out.println("CID"+cid);
		Optional<Contact> contactOptional=this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		String username = principal.getName();
		User user =this.userRepository.getUserByUserName(username);
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
		}
		return "normal/contact_details";
	}
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model,HttpSession session)
	{
	Optional<Contact> contactOptional	=this.contactRepository.findById(cid);
	Contact contact = contactOptional.get();
	contact.setUser(null);
	this.contactRepository.delete(contact);
	session.setAttribute("message", new Message("contact deleted successfully..","success"));
		return "redirect:/user/show-contacts/0";
	}
	
	//open update handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model model)
	{
		model.addAttribute("title","update contact");
		Contact contact=this.contactRepository.findById(cid).get();
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}
	//update contact handler
	@RequestMapping(value="process-update",method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file,
			Model model,Principal principal,
			HttpSession session)
	{
		try 
		{
			//old contact details
			Contact oldContactDetails=this.contactRepository.findById(contact.getCid()).get();
			
			
			if(!file.isEmpty())
			{
				//delete old photo
				File deleteFile = new ClassPathResource("/static/img").getFile();
				File file1=new File(deleteFile,oldContactDetails.getImage());
				file1.delete();
				//new file upload
				
				File saveFile = new ClassPathResource("/static/img").getFile();
				java.nio.file.Path path= Paths.get(saveFile.getAbsolutePath()+File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}
			
			else
			{
				contact.setImage(oldContactDetails.getImage());
			}
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("your contact is updated...","success"));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		System.out.println("Contact Name" + contact.getName());
		System.out.println("Contact ID" + contact.getCid());
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title","profile Page");
		return "normal/profile";
	}
	
	//Setting Handler
	@GetMapping("/settings")
	public String openSetting()
	{
		return "normal/settings";
	}
	//change setting
	@PostMapping("/change-password")
	
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,
			Principal principal, HttpSession session)
	{
		System.out.println("Old Password" + oldPassword);
		System.out.println("New Password" + newPassword);

		String userName = principal.getName();
		User user= userRepository.getUserByUserName(userName);
		if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword()))
		{
			//change password
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("password changed ..","success"));
		}
		else
		{
		   //Error message	
			session.setAttribute("message", new Message("Enter correct password","error"));
			return "redirect:/user/setttings";
		}
		return "redirect:/user/index";
	}

}
