package com.miraelDev.demo.models.authModels.tokens;

import com.miraelDev.demo.models.authModels.user.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "otpToken")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "otp_token")
    private String otpToken;

    private Long createdDate;

    @OneToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private AppUser user;

    public ConfirmationToken(AppUser user,String token) {
        this.user = user;
        this.createdDate = System.currentTimeMillis();
        this.otpToken = token;
    }
}