package com.example.amazon.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Table(name="bank_account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@BatchSize(size=5)
public class BankAccount {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name="account_number")
    @JsonProperty("account_number")
    @Size(max=17, message="Account number must not be longer than {max} characters.")
    @NotEmpty(message="Account number must not be empty.")
    private String accountNumber;

    @Column(name="bank_name")
    @JsonProperty("bank_name")
    @Size(max=255, message="Bank name must not be longer than {max} characters.")
    @NotEmpty(message="Bank name must not be empty.")
    private String bankName;

    @Column
    @NotNull
    private Date expiry;

    @Column(name="is_default")
    @JsonProperty("is_default")
    private boolean isDefault = false;

    @Column(name="is_valid")
    @JsonProperty("is_valid")
    private boolean isValid = true;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;
}
