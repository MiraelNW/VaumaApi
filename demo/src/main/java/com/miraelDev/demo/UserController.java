package com.miraelDev.demo;


import com.miraelDev.demo.servises.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/account_info")
    public ResponseEntity<?> getUserInfo(
//            @RequestParam(value = "id", required = false) Long id,
//            @RequestParam(value = "token", required = false) String token
    ) {
//        if (id != null) {
//            return userService.getAccountInfo(id);
//        } else {
            return userService.getUserInfo();
//        }

    }

    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> params) {
        return userService.changePassword(
                params.get("current_password"),
                params.get("new_password"),
                params.get("repeated_password")
        );
    }

}
