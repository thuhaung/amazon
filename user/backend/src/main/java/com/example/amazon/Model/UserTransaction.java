package com.example.amazon.Model;

import com.example.amazon.Model.Enums.TransactionStatusEnum;
import com.example.amazon.Model.Enums.UserActionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name="user_transaction")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private UserActionEnum type;

    @Column(name="referenced_auth_id", unique=true)
    @JsonProperty("referenced_auth_id")
    private Long referencedAuthId;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TransactionStatusEnum status = TransactionStatusEnum.PROCESSING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}