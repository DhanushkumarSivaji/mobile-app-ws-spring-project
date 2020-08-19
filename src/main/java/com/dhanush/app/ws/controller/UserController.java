package com.dhanush.app.ws.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhanush.app.ws.request.UserDetailsRequestModel;
import com.dhanush.app.ws.response.UserResponse;
import com.dhanush.app.ws.service.UserService;
import com.dhanush.app.ws.shared.dto.UserDto;

@RestController
@RequestMapping("users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping
	public String getUser() {
		return "User sent successfully";
	};
	
	@PostMapping
	public UserResponse createUser(@RequestBody UserDetailsRequestModel userDetails) {
		UserResponse returnValue = new UserResponse();
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnValue);
				
		return returnValue;
	};
	
	@PutMapping
	public String updateUser() {
		return "User updates successfully";
	};
	
	@DeleteMapping
	public String deleteUser() {
		return "User deleted successfully";
	};

}
