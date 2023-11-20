package com.miraelDev.demo.repositories;

import com.miraelDev.demo.models.authModels.user.AppUser;
import com.miraelDev.demo.models.authModels.tokens.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    List<RefreshToken> deleteByToken(String token);

    @Modifying
    int deleteByUser(AppUser user);
}