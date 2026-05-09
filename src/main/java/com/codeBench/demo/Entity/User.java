package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    public String getVerificationSessionId() {
        return verificationSessionId;
    }

    public void setVerificationSessionId(String verificationSessionId) {
        this.verificationSessionId = verificationSessionId;
    }

    private String provider;
    private String providerId;
    private boolean enabled = true;

    private String verificationToken;

    private String verificationSessionId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User() {}

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String username;
        private String email;
        private String password;
        private String provider;
        private Set<Role> roles;
        private boolean enabled = true;


        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder provider(String provider) { this.provider = provider; return this; }
        public Builder roles(Set<Role> roles) { this.roles = roles; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this;}

        public User build() {
            User user = new User();
            user.username = this.username;
            user.email = this.email;
            user.password = this.password;
            user.provider = this.provider;
            user.roles = this.roles;
            user.enabled=this.enabled;
            return user;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}