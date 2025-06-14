package com.kusur.Kusur.service;

import com.kusur.Kusur.repository.FriendshipRepository;
import com.kusur.Kusur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FriendshipService {
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserRepository userRepository;
}
