package com.codeBench.demo.Controller;



import com.codeBench.demo.DTO.LoginRequest;
import com.codeBench.demo.DTO.RegisterRequest;
import com.codeBench.demo.Security.JWTUtil;

import com.codeBench.demo.Services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.web.bind.annotation.*;


import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil, AuthService authService) {

        this.authService=authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorize(@RequestBody LoginRequest request) {

        return authService.authorize(request);

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        return authService.register(registerRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {

        return ResponseEntity.ok(authService.refreshToken(request.get("refreshToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {

        authService.logout(request.get("refreshToken"));
        return ResponseEntity.ok("Logged out");
    }



    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
       return authService.verify(token);
    }

}
