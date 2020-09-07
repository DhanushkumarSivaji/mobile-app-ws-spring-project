package com.dhanush.app.ws.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dhanush.app.ws.exceptions.UserServiceException;
import com.dhanush.app.ws.request.UserDetailsRequestModel;
import com.dhanush.app.ws.response.AddressesResponse;
import com.dhanush.app.ws.response.ErrorMessages;
import com.dhanush.app.ws.response.OperationStatusModel;
import com.dhanush.app.ws.response.RequestOperationStatus;
import com.dhanush.app.ws.response.UserResponse;
import com.dhanush.app.ws.service.AddressService;
import com.dhanush.app.ws.service.UserService;
import com.dhanush.app.ws.shared.dto.AddressDto;
import com.dhanush.app.ws.shared.dto.UserDto;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressesService;

	// MediaType.APPLICATION_JSON_VALUE - SEND RESPONSE IN JSON , ORDER MATTERS IF
	// XML IS IMPLEMENTED IN FIRST IT WILL SEND XML AS DEFAULT.
	@GetMapping(path = "/{id}")
	public UserResponse getUser(@PathVariable String id) {
		UserResponse returnValue = new UserResponse();
		UserDto userDto = userService.getUserByUserId(id);
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(userDto, UserResponse.class);

		return returnValue;
	};

	@PostMapping
	public UserResponse createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserResponse returnValue = new UserResponse();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserResponse.class);

		return returnValue;
	};

	@PutMapping(path = "/{id}")
	public UserResponse updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserResponse returnValue = new UserResponse();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	};

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);

		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	};

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "5") int limit) {
		List<UserResponse> returnValue = new ArrayList<>();

		List<UserDto> users = userService.getUsers(page, limit);

		Type listType = new TypeToken<List<UserResponse>>() {
		}.getType();
		returnValue = new ModelMapper().map(users, listType);

//		for (UserDto userDto : users) {
//			UserResponse userModel = new UserResponse();
//			BeanUtils.copyProperties(userDto, userModel);
//			returnValue.add(userModel);
//		}

		return returnValue;
	}

	// http://localhost:8080/mobile-app-ws/users/jfhdjeufhdhdj/addressses

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, })
	public CollectionModel<AddressesResponse> getUserAddresses(@PathVariable String id) {
		List<AddressesResponse> returnValue = new ArrayList<>();

		List<AddressDto> addressesDTO = addressesService.getAddresses(id);

		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressesResponse>>() {
			}.getType();
			ModelMapper modelMapper = new ModelMapper();
			returnValue = new ModelMapper().map(addressesDTO, listType);
			
			for (AddressesResponse addressRest : returnValue) {
				Link addressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
									.getUserAddress(id,addressRest.getAddressId()))
									.withSelfRel();
				addressRest.add(addressLink);

			
			}

		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
						.withSelfRel();
		

		return CollectionModel.of(returnValue,userLink,selfLink);
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public EntityModel<AddressesResponse> getUserAddress(@PathVariable String userId,@PathVariable String addressId) {

		AddressDto addressesDTO = addressesService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();

		AddressesResponse returnValue = modelMapper.map(addressesDTO , AddressesResponse.class);
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
		Link userAddressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)).withRel("addresses");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId,addressId))
				.withSelfRel();

		returnValue.add(userLink);
		returnValue.add(userAddressLink);
		returnValue.add(selfLink);

		return new EntityModel<>(returnValue);
	}

}
