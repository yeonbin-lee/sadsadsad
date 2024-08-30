package com.example.common_module.domain.member.entity;

import jakarta.persistence.*;

@Entity
public class Profile {

    @Id
    @Column(name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
