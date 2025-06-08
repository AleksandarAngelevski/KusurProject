package com.kusur.Kusur.service;

import com.kusur.Kusur.dto.UserRegistrationDto;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.kusur.Kusur.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.kusur.Kusur.util.UniqueTagGenerator;

import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService  implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
//    @Autowired
//    private UserDetailsPasswordService userDetailsPasswordService;

    public User registerNewUser(UserRegistrationDto dto){
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }
        if(userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        user.setEnabled(false);
        user.setVerificationToken(generateVerificationToken());
        String uniqueId = UniqueTagGenerator.generateUniqueTag(dto.getUsername());
        while(userRepository.findByUniqueId(uniqueId).isPresent()){
            uniqueId = UniqueTagGenerator.generateUniqueTag(dto.getUsername());
        }
        user.setUniqueId(uniqueId);


        // Send email

        emailService.sendVerificationEmail(user);
        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    public String generateVerificationToken(){
        return UUID.randomUUID().toString();
    }


}
