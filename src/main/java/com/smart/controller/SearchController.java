package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.Contact;
import com.smart.User;
import com.smart.Dao.ContactRepository;
import com.smart.Dao.UserRepository;

@RestController
public class SearchController {
	//Search Handler
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@GetMapping("/search/{query}")
	public ResponseEntity<?> Search(@PathVariable("query") String query,Principal principal)
	{
		System.out.println(query);
		User user = this.userRepository.getUserByUserName(principal.getName());
		List<Contact> contacts =this.contactRepository.findByNameContainingAndUser(query,user);
		
		return ResponseEntity.ok(contacts);
	}

}
