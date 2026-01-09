package com.aprendendoSpring.course.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aprendendoSpring.course.entities.User;

@RestController
@RequestMapping(value="/users")
public class UserResource {

	@GetMapping
	public ResponseEntity<User> findAll(){
		User u = new User(1L, "Maria", "maria@gmail533.com", "9999-9999","12345");
		
		return ResponseEntity.ok().body(u);
	}
	
}
