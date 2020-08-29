package com.dhanush.app.ws.service.implementation;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import com.dhanush.app.ws.entity.UserEntity;
import com.dhanush.app.ws.exceptions.UserServiceException;
import com.dhanush.app.ws.repository.UserRepository;
import com.dhanush.app.ws.response.ErrorMessages;
import com.dhanush.app.ws.service.UserService;
import com.dhanush.app.ws.shared.dto.UserDto;
import com.dhanush.app.ws.shared.dto.shared.Utils;



@Service
public class UserServiceImplementation implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		
		if(userRepository.findByEmail(user.getEmail()) != null ) throw new RuntimeException("User already exists");
		
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		String publicUserId = utils.generateUserId(30);
       
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setUserId(publicUserId);
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		if(userEntity == null) throw new UsernameNotFoundException(email);
				
		return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
	}
	
	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
 
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) throw new UsernameNotFoundException("User with ID: " + userId + " not found");
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {
		UserDto returnValue = new UserDto();

		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());

		UserEntity updatedUserDetails = userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedUserDetails, returnValue);

		return returnValue;

	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userRepository.delete(userEntity);
		
	}

}
