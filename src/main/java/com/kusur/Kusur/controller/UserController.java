package com.kusur.Kusur.controller;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/users")
    public Iterable<User> findAllUsers(){
        return this.userRepository.findAll();
    }
    @PostMapping("/users")
    public User addUser(@RequestBody User user){
        return this.userRepository.save(user);
    }
    @GetMapping("/getUserDetails/{username}")
    public UserDetailsDto getUserDetails(@PathVariable String username){

        User user = this.userRepository.findByUsername(username).orElse(null);
        System.out.println(username);
        return new UserDetailsDto(user.getUsername(),user.getEmail(),user.getNickname());
    }

}
