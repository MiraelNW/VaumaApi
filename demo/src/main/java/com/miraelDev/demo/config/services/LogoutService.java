package com.miraelDev.demo.config.services;

import com.miraelDev.demo.models.authModels.tokens.RefreshToken;
import com.miraelDev.demo.models.authModels.user.AppUser;
import com.miraelDev.demo.repositories.RefreshTokenRepository;
import com.miraelDev.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    @Autowired
    private RefreshTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);

        Optional<RefreshToken> refreshTokenOptional = tokenRepository.findByToken(jwt);

        if (refreshTokenOptional.isPresent()) {

            AppUser user = refreshTokenOptional.get().getUser();

            user.setEnabled(false);
            userRepository.save(user);
            tokenRepository.deleteByToken(jwt);
        }
    }
}
