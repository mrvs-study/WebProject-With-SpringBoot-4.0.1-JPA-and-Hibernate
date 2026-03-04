package com.aprendendoSpring.course.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.aprendendoSpring.course.entities.User;
import com.aprendendoSpring.course.repositories.UserRepository;
import com.aprendendoSpring.course.service.exceptions.DatabaseException;
import com.aprendendoSpring.course.service.exceptions.ResourceNotFoundExceptions;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;

	public List<User> findAll(){
		return repository.findAll();
		
	}
	
	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundExceptions(id));
	}

	public User insert(User obj) {
		return repository.save(obj);
	}
	
	public void delete(long id) {
	
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundExceptions(id);
		} 
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
		
	}
	
	public User update(long id, User obj) {
		
		try {
			User entity=repository.getReferenceById(id);
		updateData(entity,obj);
		return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundExceptions(id);
		}
				
	}

	private void updateData(User entity, User obj) {
		entity.setName(obj.getName());
		entity.setEmail(obj.getEmail());
		entity.setPhone(obj.getPhone());
	}
	
}
