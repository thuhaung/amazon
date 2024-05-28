package com.example.amazon.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity(name="role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="type")
    private String type;

    @ManyToMany(fetch=FetchType.LAZY, mappedBy="roles")
    @JsonIgnore
    private List<AuthUser> users;

    @Override
    public String getAuthority() {
        return this.type;
    }
}
