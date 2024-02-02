package com.smart.Dao;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.Contact;
import com.smart.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>
{
	//pagination
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable);
	//Search
	public List<Contact> findByNameContainingAndUser(String name, User user);
	


}
