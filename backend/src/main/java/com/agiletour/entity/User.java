package com.agiletour.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String username;

    private String password;

    private String fullName;
}
