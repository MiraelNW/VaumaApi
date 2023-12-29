package com.miraelDev.demo.servises;


import com.miraelDev.demo.models.authModels.user.AppUser;
import com.miraelDev.demo.models.responseDto.UserInfoDto;
import com.miraelDev.demo.repositories.RefreshTokenRepository;
import com.miraelDev.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    PasswordEncoder encoder;

//    public ResponseEntity<?> getAccountInfo(Long id) {
//        return mapUserDbToDtoModel(userRepository.findById(id));
//    }

    public ResponseEntity<?> getUserInfo() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<AppUser> optUser = userRepository.findByUsername(username);

        if (optUser.isPresent()) {
            AppUser user = optUser.get();
            return mapUserDbToDtoModel(user);
        } else {
            return ResponseEntity.badRequest().body("not find person by username");
        }
    }

    public ResponseEntity<?> changePassword(String currentPassword, String newPassword, String repeatedPassword) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<AppUser> optUser = userRepository.findByUsername(username);

        if (optUser.isPresent()) {

            AppUser user = optUser.get();

            if (encoder.matches(currentPassword, user.getPassword()) && newPassword.equals(repeatedPassword) && isValidPassword(newPassword)) {
                user.setPassword(encoder.encode(newPassword));
                userRepository.save(user);
                return ResponseEntity.ok("save successfully");
            }
        }
        return ResponseEntity.badRequest().body("bad cred");
    }

    private Boolean isValidPassword(String password) {
        return password.matches(".*[A-Z,А-Я].*") && password.matches(".{6,}");
    }

    private ResponseEntity<?> mapUserDbToDtoModel(AppUser user) {
        UserInfoDto dto = new UserInfoDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getImagePath()
        );
        return ResponseEntity.ok(dto);
    }

}
