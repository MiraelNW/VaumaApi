package com.miraelDev.demo.models.authModels.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class AppUser {

    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String imagePath;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private Boolean locked = false;
    private Boolean enabled = false;

    @ElementCollection
    @CollectionTable(
            name = "favourite_anime",
            joinColumns = @JoinColumn(name = "anime_id")
    )
    private Set<Long> animeFavouriteList = new HashSet<>();

    public AppUser(
            String name,
            String username,
            String email,
            String password,
            AppUserRole appUserRole
    ) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.appUserRole = appUserRole;
    }
}