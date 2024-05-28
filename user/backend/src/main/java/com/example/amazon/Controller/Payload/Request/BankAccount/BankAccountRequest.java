package com.example.amazon.Controller.Payload.Request.BankAccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountRequest {
    @JsonProperty("account_number")
    @Size(max=17, message="Account number must not be longer than {max} characters.")
    @NotEmpty(message="Account number must not be empty.")
    private String accountNumber;

    @JsonProperty("bank_name")
    @Size(max=255, message="Bank name must not be longer than {max} characters.")
    @NotEmpty(message="Bank name must not be empty.")
    private String bankName;

    @Future(message="Account must not be expired.")
    private Date expiry;

    @JsonProperty("is_default")
    private boolean isDefault = false;
}
