package com.example.amazon.Model;

import com.example.amazon.Model.Enums.AddressEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Index;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@BatchSize(size=5)
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    @Size(max=255, message="Building name must not be longer than {max} characters.")
    private String building;

    @Column
    @Size(max=255, message="Street name must not be longer than {max} characters.")
    private String street;

    @Column
    @Size(max=255, message="City name must not be longer than {max} characters.")
    @NotEmpty(message="City must not be empty.")
    private String city;

    @Column
    @Size(max=255, message="Region name must not be longer than {max} characters.")
    @NotEmpty(message="Region/state must not be empty.")
    private String region;

    @Column
    @Size(max=255, message="Country name must not be longer than {max} characters.")
    @NotEmpty(message="Country must not be empty.")
    private String country;

    @Column(name="postal_code")
    @JsonProperty("postal_code")
    @Size(max=5, message="Postal code cannot be longer than {max} characters.")
    private String postalCode;

    @Column(name="is_default")
    @JsonProperty("is_default")
    private boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    @Column(name="address_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @JsonProperty("address_type")
    private AddressEnum addressType;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;
}
