package com.example.amazon.Model;

import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"credentialsNonExpired", "accountNonLocked", "authorities", "username", "enabled", "accountNonExpired"})
public class AuthUser implements UserDetails {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="email", unique=true)
    @Size(max=256, message="Email must not be longer than {max} characters.")
    @NotEmpty(message="Email cannot be empty.")
    private String email;

    @Column(name="password")
    @JsonIgnore
    @Size(min=5, message="Password must be longer than {min} characters.")
    @NotEmpty(message="Password cannot be empty.")
    private String password;

    @Column(name="is_verified")
    @JsonProperty("is_verified")
    private boolean isVerified = false;

    @Column(name="is_active")
    @JsonProperty("is_active")
    private boolean isActive = true;

    @Column(name="deletion_date")
    @JsonProperty("deletion_date")
    private Date deletionDate;

    @ManyToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name="auth_user_role",
        joinColumns=@JoinColumn(name="auth_user_id", referencedColumnName="id"),
        inverseJoinColumns=@JoinColumn(name="role_id", referencedColumnName="id")
    )
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role: this.roles) {
            authorities.add(new SimpleGrantedAuthority(role.getType()));
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
