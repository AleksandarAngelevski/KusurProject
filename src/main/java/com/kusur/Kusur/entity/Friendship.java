package com.kusur.Kusur.entity;

import jakarta.persistence.*;
// Doprva ke ja pisham
@Entity
public class Friendship {
    @Id
    @GeneratedValue
    private Long Id;

    @ManyToOne
    private User requester;
    @ManyToOne
    private User recipient;

}
