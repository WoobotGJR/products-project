package com.woobot.springproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// To avoid conflict with Spring security user, our user called CustomUser

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user", schema = "user_management")
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "c_username")
    private String username;

    @Column(name = "c_password")
    private String password;

    @ManyToMany
//    @ManyToMany(fetch = FetchType.EAGER) // to make it load, when the user info loads
    @JoinTable(schema = "user_management", name = "t_user_authority",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_authority"))
    private List<Authority> authorities;
}
