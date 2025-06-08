package com.kusur.Kusur.controller;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.entity.User;
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
}
