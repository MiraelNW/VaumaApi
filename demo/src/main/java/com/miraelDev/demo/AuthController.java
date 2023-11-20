package com.miraelDev.demo;

import com.miraelDev.demo.models.authModels.user.AppUser;
import com.miraelDev.demo.models.authModels.user.AppUserRole;
import com.miraelDev.demo.payload.SignInModel;
import com.miraelDev.demo.payload.SignUpModel;
import com.miraelDev.demo.payload.TokenRefreshRequest;
import com.miraelDev.demo.servises.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthService authService;

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser(@RequestBody SignInModel loginRequest) {
        System.out.println(loginRequest);
        return authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(value = "file",required = false) MultipartFile userImage
    ) {

        AppUser user = new AppUser(
                username,
                email,
                encoder.encode(password),
                AppUserRole.USER
        );

        return authService.registerUser(user, userImage);
    }

    @PostMapping(value = "verify_otp", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmOtp(@RequestBody SignUpModel signUpRequest) {
        return authService.confirmOtpForSignUp(signUpRequest.getOtpToken());
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return authService.refreshToken(requestRefreshToken);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> params) {
        return authService.checkEmail(params.get("email"));
    }

    @PostMapping("/verify_otp_forgot_password")
    public ResponseEntity<?> verifyOtpForgotPassword(@RequestBody Map<String, String> params) {
        return authService.confirmOtpForForgotPassword(params.get("token"));
    }

    @PostMapping("/new_password")
    public ResponseEntity<?> createNewPassword(@RequestBody Map<String, String> params) {
        return authService.createNewPassword(params.get("email"), params.get("password"));
    }

    @PostMapping("/google_login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> params) throws GeneralSecurityException, IOException {
        return authService.loginWithGoogle(params.get("idToken"));
    }

    @PostMapping("/vk_login")
    public ResponseEntity<?> loginWithVk(@RequestBody Map<String, String> params) throws GeneralSecurityException, IOException {
        return authService.loginWithVk(params.get("accessToken"),params.get("userId"),params.get("email"));
    }

    @PostMapping("/google_login_success")
    public ResponseEntity<?> loginWithGoogleSuccessCallback(@RequestBody Map<String, String> params) {
        return ResponseEntity.ok("ok");
    }

}