package com.miraelDev.demo.servises;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.miraelDev.demo.config.secure.JwtUtils;
import com.miraelDev.demo.config.services.RefreshTokenService;
import com.miraelDev.demo.config.services.UserDetailsImpl;
import com.miraelDev.demo.exceptions.TokenRefreshException;
import com.miraelDev.demo.models.authModels.tokens.ConfirmationToken;
import com.miraelDev.demo.models.authModels.tokens.RefreshToken;
import com.miraelDev.demo.models.authModels.user.AppUser;
import com.miraelDev.demo.models.authModels.user.AppUserRole;
import com.miraelDev.demo.payload.*;
import com.miraelDev.demo.repositories.ConfirmationTokenRepository;
import com.miraelDev.demo.repositories.UserRepository;
import com.miraelDev.demo.servises.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthService {

    @Value("${vauma.app.googleClientId}")
    String clientId;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder encoder;

    public ResponseEntity<?> authenticateUser(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        refreshToken.getToken()
                )
        );
    }

    public ResponseEntity<?> registerUser(AppUser user, MultipartFile imageFile) {


        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        if (imageFile != null && !Objects.requireNonNull(imageFile.getOriginalFilename()).isEmpty()) {
            downloadFiles(imageFile, "C:\\Users\\1\\Desktop\\users\\image\\" + "admin" + ".png");
        }

        user.setImagePath("http://10.0.2.2:8080/api/v1/users/image/" + user.getUsername());

        userRepository.save(user);

        String code = generateCode();

        emailService.send(user.getEmail(), "verify code " + code);

        confirmationTokenRepository.save(new ConfirmationToken(user, code));

        return ResponseEntity.ok(new MessageResponse("Confirm email was send"));
    }

    public ResponseEntity<?> loginWithGoogle(String idTokenString) throws GeneralSecurityException, IOException {

        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
        GoogleIdToken idToken = verifier.verify(idTokenString);

        System.out.println(idToken);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String username = email.split("@")[0];

            if (!userRepository.existsByUsername(username)) {

                AppUser user = new AppUser(
                        name,
                        username,
                        email,
                        null,
                        AppUserRole.USER
                );

                userRepository.save(user);
            }

            return authWithGoogle(email);
        }
        return ResponseEntity.badRequest().body("not found token");
    }

    public ResponseEntity<?> loginWithVk(String accessToken, String userId, String email) {
        VkResponse response = WebClient.create("https://api.vk.com/method/users.get?user_ids=" + userId + "&v=5.131")
                .get()
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(VkResponse.class)
                .single()
                .block();

        if (response == null || response.getResponse()[0] == null)
            ResponseEntity.badRequest().body("not found account");

        VkProfile profile = Objects.requireNonNull(response).getResponse()[0];

        String username = profile.getFirst_name() + profile.getLast_name();

        if (!userRepository.existsByUsername(username)) {

            System.out.println(email == null);

            AppUser user = new AppUser(
                    profile.getFirst_name(),
                    username,
                    email,
                    null,
                    AppUserRole.USER
            );

            userRepository.save(user);
        }

        return authWithVk(username);
    }

    public ResponseEntity<?> confirmOtpForSignUp(String otpToken) {

        ConfirmationToken token = confirmationTokenRepository.findByOtpToken(otpToken);

        if (token != null) {
            AppUser user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);
            userRepository.save(user);
            confirmationTokenRepository.deleteByOtpToken(token.getOtpToken());
            return ResponseEntity.ok("Email verified successfully!");
        }

        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }

    public ResponseEntity<?> refreshToken(String requestRefreshToken) {
        System.out.println(requestRefreshToken);
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() ->
                        new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!")
                );
    }

    public ResponseEntity<?> checkEmail(String email) {

        AppUser user = userRepository.findByEmailIgnoreCase(email);

        if (user != null) {
            String code = generateCode();

            emailService.send(email, "verify code " + code);

            confirmationTokenRepository.save(new ConfirmationToken(user, code));

            return ResponseEntity.ok("user exist");
        } else {
            return ResponseEntity.badRequest().body("user doesn't exist");
        }
    }

    public ResponseEntity<?> confirmOtpForForgotPassword(String otpToken) {

        ConfirmationToken token = confirmationTokenRepository.findByOtpToken(otpToken);

        if (token != null) {
            return ResponseEntity.ok("Email verified successfully!");
        }

        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }

    public ResponseEntity<?> createNewPassword(String email, String password) {

        AppUser user = userRepository.findByEmailIgnoreCase(email);

        user.setPassword(encoder.encode(password));
        user.setEnabled(true);

        userRepository.save(user);

        String username = email.split("@")[0];


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        refreshToken.getToken()
                )
        );
    }

    private String generateCode() {
        Random random = new Random();
        int[] num = new int[4];
        StringBuilder res = new StringBuilder();
        int rand = random.nextInt(10);
        for (int i = 0; i < 4; i++) {
            while (!checkOnRepeat(num, rand))
                rand = random.nextInt(10);
            num[i] = rand;
            rand = random.nextInt(10);
            res.append(num[i]);
        }

        return res.toString();
    }

    private ResponseEntity<?> authWithGoogle(String email) {

        AppUser user = userRepository.findByEmailIgnoreCase(email);
        user.setEnabled(true);
        userRepository.save(user);

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();
        Long userId = user.getId();

        if (!username.equals(user.getUsername())) ResponseEntity.badRequest();

        String jwt = jwtUtils.generateTokenFromUsername(username);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        refreshToken.getToken()
                )
        );
    }

    private ResponseEntity<?> authWithVk(String username) {

        Optional<AppUser> optUser = userRepository.findByUsername(username);

        if (optUser.isPresent()) {

            AppUser user = optUser.get();

            user.setEnabled(true);
            userRepository.save(user);

            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("USER"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Long userId = user.getId();

            String jwt = jwtUtils.generateTokenFromUsername(authentication.getName());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

            return ResponseEntity.ok(
                    new JwtResponse(
                            jwt,
                            refreshToken.getToken()
                    )
            );
        } else {
            return ResponseEntity.badRequest().body("user is not present in db");
        }
    }

    private boolean checkOnRepeat(int[] num, Integer rand) {
        for (int i : num)
            if (i == rand)
                return false;

        return true;
    }

    private static void downloadFiles(MultipartFile file, String path) {
        if (!file.isEmpty()) {
            try {
                InputStream in = null;
                in = file.getInputStream();
                OutputStream writer = new FileOutputStream(path);
                byte[] buffer = new byte[1];
                int c = in.read(buffer);
                while (c > 0) {
                    writer.write(buffer, 0, c);
                    c = in.read(buffer);
                }
                writer.flush();
                writer.close();
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}