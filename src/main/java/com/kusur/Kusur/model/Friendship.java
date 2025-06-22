package com.kusur.Kusur.model;

import jakarta.persistence.*;
@Entity
public class Friendship {
    @Id
    @GeneratedValue
    private Long Id;

    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Sender: "+ this.sender.getUsername()+"\n Receiver: "+this.receiver.getUsername();
    }
}
