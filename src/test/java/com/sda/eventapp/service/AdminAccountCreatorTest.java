package com.sda.eventapp.service;

import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAccountCreatorTest {
   @InjectMocks
   AdminAccountCreator adminAccountCreator;

   @Mock
   PasswordEncoder passwordEncoder;

   @Mock
   UserRepository userRepository;

   User userTest;

   @BeforeEach
   void beforeEach(){
      userTest = new User();
   }

   @Test
   void shouldNotAddAdminToDatabaseIfExists(){
      when(userRepository.existsByEmail(null)).thenReturn(true);
      adminAccountCreator.createAdminAccount();
      verify(userRepository, times(0)).save(userTest);
   }

   @Test
   void shouldAddAdminToDatabaseIfNotExist(){
      when(userRepository.existsByEmail(null)).thenReturn(false);
      when(passwordEncoder.encode(null)).thenReturn(anyString());
      adminAccountCreator.createAdminAccount();
      verify(userRepository, times(1)).save(userTest);
   }
}