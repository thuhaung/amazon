package com.example.amazon.Model;

import com.example.amazon.Model.Enum.AuthUserActionEnum;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name="auth_user_transaction")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserTransaction {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AuthUserActionEnum type;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TransactionStatusEnum status = TransactionStatusEnum.PENDING;

    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name="auth_user_id", referencedColumnName="id", nullable=false)
    private AuthUser user;
}
