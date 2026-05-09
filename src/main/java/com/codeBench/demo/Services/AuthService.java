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
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.authentication.DisabledException;


import java.time.Instant;
import java.util.List;
import java.util.Map;
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
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public AuthService(UserDao userDao,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil,
                       RefreshTokenRepository refreshTokenRepository,
                       AuthenticationManager authenticationManager,
                       EmailService emailService,
                       SimpMessagingTemplate messagingTemplate) {
        this.userDao = userDao;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil=jwtUtil;
        this.refreshTokenRepository=refreshTokenRepository;
        this.authenticationManager=authenticationManager;
        this.emailService=emailService;
        this.messagingTemplate=messagingTemplate;
    }

    public ResponseEntity<?> register(RegisterRequest request) {

        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Username already exists"));
        }

        if (userDao.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Email already exists"));
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role role = new Role((long) 1, "ROLE_USER");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .enabled(false)
                .roles(Set.of(role))
                .build();
        String token = UUID.randomUUID().toString();
        String verificationSessionId=UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationSessionId(verificationSessionId);

        userDao.save(user);

        try {

            emailService.sendVerificationEmail(
                    user.getEmail(),
                    token
            );

        } catch (MessagingException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(500)
                    .body(Map.of(
                            "message",
                            "Failed to send email"
                    ));
        }
        return ResponseEntity.ok(Map.of("message","Registration successful. Please verify your email.","verificationSessionId",verificationSessionId));
    }
    @Transactional
    public ResponseEntity<?> authorize(@RequestBody LoginRequest request) {
        try {



            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUserName(),
                                    request.getPassword()
                            )
                    );



            refreshTokenRepository.deleteByUsername(
                    request.getUserName()
            );



            String username = authentication.getName();



            List<String> roles =
                    authentication.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();


            String accessToken =
                    jwtUtil.generateToken(
                            username,
                            roles
                    );



            RefreshToken refreshToken =
                    createRefreshToken(username);

            return ResponseEntity.ok(
                    new LoginResponse(
                            "Login Successful",
                            username,
                            accessToken,
                            refreshToken.getToken()
                    )
            );

        } catch (DisabledException e) {



            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "Please verify your email first"
                    ));

        } catch (BadCredentialsException e) {

            // WRONG USERNAME OR PASSWORD

            return ResponseEntity
                    .status(401)
                    .body(Map.of(
                            "message",
                            "Invalid username or password"
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(500)
                    .body(Map.of(
                            "message",
                            "Something went wrong"
                    ));
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

    public String verify(String token){
        User user = userDao.findByVerificationToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid token"));

        user.setEnabled(true);

        user.setVerificationToken(null);

        userDao.save(user);

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getRole())
                .toList();

        String accessToken =
                jwtUtil.generateToken(
                        user.getUsername(),
                        roles
                );

        RefreshToken refreshToken =
                createRefreshToken(user.getUsername());

        LoginResponse response = new LoginResponse(
                "Login successful",
                user.getUsername(),
                accessToken,
                refreshToken.getToken()
        );

        messagingTemplate.convertAndSend(
                "/topic/verification/" +
                        user.getVerificationSessionId(),
                response
        );

        user.setVerificationSessionId(null);

        userDao.save(user);

        return "Email verified successfully";
    }
}
