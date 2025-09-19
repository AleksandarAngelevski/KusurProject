package com.kusur.Kusur.service;

import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.Friendship;
import com.kusur.Kusur.model.FriendshipStatus;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.FriendshipRepository;
import com.kusur.Kusur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<UserDetailsDto> getAllFriends(User user){
        List<User> f1 = new ArrayList<>(friendshipRepository.findFriendshipsBySender(user).stream().map(f -> f.getReceiver()).toList());
        List<User> f2 = friendshipRepository.findFriendshipsByReceiver(user).stream().map( f -> f.getSender()).toList();
        f1.addAll(f2);
        List<UserDetailsDto> userDetailsDtos = f1.stream().map((u)-> new UserDetailsDto(u.getUsername(),u.getEmail(),u.getNickname())).collect(Collectors.toList());
        return userDetailsDtos;
    }
    public boolean friendshipExists(String user, String receiver){
        User s = userRepository.findByUsername(user).orElseThrow(()-> new UsernameNotFoundException(user+" User not found"));
        User r = userRepository.findByUsername(receiver).orElseThrow(()-> new UsernameNotFoundException(receiver+ " User not found"));
        return friendshipRepository.existsBySenderAndReceiver(s,r) || friendshipRepository.existsBySenderAndReceiver(r,s);
    }
}

