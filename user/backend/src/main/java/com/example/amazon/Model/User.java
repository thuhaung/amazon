package com.example.amazon.Model;

import com.example.amazon.Model.Enums.GenderEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="users")
public class User {

    @Id
    @Column(unique=true)
    private Long id;

    @Column
    @Size(max=255, message="Name must not be longer than {max} characters.")
    private String name;

    @Column(name="birth_date")
    @Past(message="Birthdate must be in the past.")
    @JsonProperty("birth_date")
    private Date birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name="gender")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @JsonProperty("gender")
    private GenderEnum genderEnum;

    @Column(unique=true)
    @Size(max=30, message="Mobile number must not be longer than {max} characters.")
    private String mobile;

    @Column(name="name_changed_at")
    @JsonProperty("name_changed_at")
    @PastOrPresent
    private Date nameChangedAt;

    @Column(name="birth_date_changed_at")
    @JsonProperty("birth_date_changed_at")
    @PastOrPresent
    private Date birthDateChangedAt;

    @Column(name="is_vendor")
    @JsonProperty("is_vendor")
    @NotNull
    private boolean isVendor = false;
}
