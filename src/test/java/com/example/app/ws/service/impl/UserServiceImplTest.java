package com.example.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.app.ws.exceptions.UserServiceException;
import com.example.app.ws.io.entity.AddressEntity;
import com.example.app.ws.io.entity.UserEntity;
import com.example.app.ws.io.repositories.UserRepository;
import com.example.app.ws.shared.Utils;
import com.example.app.ws.shared.dto.AddressDto;
import com.example.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	String userId = "lsdf9fjksldfl";
	String encryptedPassword = "bla35bal394bla";

	UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Saiyem");
		userEntity.setLastName("Bejoy");
		userEntity.setEmail("test@test.com");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	void testGetUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = userService.getUser("test@test.com");

		assertNotNull(userDto);
		assertEquals("Saiyem", userDto.getFirstName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows(UsernameNotFoundException.class,

				() -> {
					userService.getUser("test@test.com");
				}

		);
	}
	
	
	@Test
	final void testCreateUser_CreateUserServiceException() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Saiyem");
		userDto.setLastName("Bejoy");
		userDto.setPassword("123456");
		userDto.setEmail("test@test.com");
		
		assertThrows(
				UserServiceException.class,

				() -> {
					userService.createUser(userDto);
				}

		);
	}
	
	
	

	@Test
	final void testCreateUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("hgslfhdfjoe");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Saiyem");
		userDto.setLastName("Bejoy");
		userDto.setPassword("123456");
		userDto.setEmail("test@test.com");

		UserDto storedUserDetails = userService.createUser(userDto);

		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		
		verify(utils,times(storedUserDetails.getAddresses().size())).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("123456");
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	private List<AddressDto> getAddressesDto() {
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("Dhaka");
		addressDto.setCountry("Bangladesh");
		addressDto.setPostalCode("1755");
		addressDto.setStreetName("DoyelChottor");

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Dhaka");
		billingAddressDto.setCountry("Bangladesh");
		billingAddressDto.setPostalCode("1755");
		billingAddressDto.setStreetName("DoyelChottor");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);

		return addresses;
	}
	
	private List<AddressEntity> getAddressesEntity(){
		List<AddressDto> addresses = getAddressesDto();
		
		Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		
		return new ModelMapper().map(addresses, listType);
	}
}








