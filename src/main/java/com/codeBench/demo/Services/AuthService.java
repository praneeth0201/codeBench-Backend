package com.codeBench.demo.Services;


import com.codeBench.demo.DAO.RefreshTokenRepository;
import com.codeBench.demo.DAO.RoleRepository;
import com.codeBench.demo.DAO.UserDao;
import com.codeBench.demo.DTO.LoginRequest;
import com.codeBench.demo.DTO.LoginResponse;
import com.codeBench.demo.DTO.RegisterRequest;
import com.codeBench.demo.Entity.RefreshToken;
import com.codeBench.demo.Entity.Role;
import com.codeBench.demo.Entity.User;
import com.codeBench.demo.Security.JWTUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;


import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final UserDao userDao;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    public AuthService(UserDao userDao,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil,
                       RefreshTokenRepository refreshTokenRepository,
                       AuthenticationManager authenticationManager) {
        this.userDao = userDao;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil=jwtUtil;
        this.refreshTokenRepository=refreshTokenRepository;
        this.authenticationManager=authenticationManager;
    }

    public LoginResponse register(RegisterRequest request) {


        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }


        if (userDao.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }


        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role role=new Role((long)1,"ROLE_USER");



        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .enabled(true)
                .roles(Set.of(role))
                .build();


        userDao.save(user);

        List<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getRole())
                .toList();

        String accessToken = jwtUtil.generateToken(user.getUsername(), roles);
        RefreshToken refreshToken=createRefreshToken(user.getUsername());

        return new LoginResponse(
                "User registered successfully",
                user.getUsername(),
                accessToken,
                refreshToken.getToken()
        );
    }
    @Transactional
    public ResponseEntity<?> authorize(@RequestBody LoginRequest request) {
        refreshTokenRepository.deleteByUsername(request.getUserName());

        try {
            Authentication status = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );


            String userName = status.getName();

            List<String> roles = status.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String accessToken = jwtUtil.generateToken(userName, roles);
            RefreshToken refreshToken = createRefreshToken(userName);
            return ResponseEntity.ok(new LoginResponse("Login Successfull", userName, accessToken,refreshToken.getToken()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Authentication Failed: " + e.getMessage());
        }

    }

    public LoginResponse refreshToken(String oldToken) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new RuntimeException("Token revoked");
        }

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        String username = storedToken.getUsername();


        refreshTokenRepository.delete(storedToken);


        RefreshToken newRefreshToken = createRefreshToken(username);


        String newAccessToken = jwtUtil.generateToken(username, List.of("ROLE_USER"));

        return new LoginResponse("token refresh",username,newAccessToken, newRefreshToken.getToken());
    }

    public void logout(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken token = new RefreshToken();
        token.setUsername(username);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }
}
