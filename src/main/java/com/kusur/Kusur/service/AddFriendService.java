package com.kusur.Kusur.service;

import com.kusur.Kusur.model.Friendship;
import com.kusur.Kusur.model.FriendshipStatus;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.FriendshipRepository;
import com.kusur.Kusur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddFriendService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    public void sendFriendRequest(String sender,String receiver){
        User senderObj = userRepository.findByUsername(sender).orElseThrow(()-> new RuntimeException("Sender not found")); // WHY DO I EVEN NEED THIS
        User receiverObj = userRepository.findByUsername(receiver).orElseThrow(()-> new RuntimeException("Receiver not found"));
        if(friendshipRepository.existsBySenderAndReceiver(senderObj,receiverObj)){
            throw new IllegalStateException("Friend request already exists");
        }
        Friendship friendship = new Friendship();
        friendship.setSender(senderObj);
        friendship.setReceiver(receiverObj);
        friendship.setStatus(FriendshipStatus.FRIENDS);

        friendshipRepository.save(friendship);
    }
}

