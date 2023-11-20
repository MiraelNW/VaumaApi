package com.miraelDev.demo.repositories;

import com.miraelDev.demo.models.authModels.tokens.ConfirmationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository()
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    ConfirmationToken findByOtpToken(String confirmationToken);

    @Transactional
    Integer deleteByOtpToken(String confirmationToken);

}