package com.example.amazon.DTO.BankAccount;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountDTO {
    @JsonProperty("account_number")
    @Size(max=17, message="Account number must not be longer than {max} characters.")
    @NotEmpty(message="Account number must not be empty.")
    private String accountNumber;

    @JsonProperty("bank_name")
    @Size(max=255, message="Bank name must not be longer than {max} characters.")
    @NotEmpty(message="Bank name must not be empty.")
    private String bankName;

    @NotNull(message="Bank account expiry must be specified.")
    private Date expiry;

    @JsonProperty("is_default")
    private boolean isDefault = false;

    @JsonProperty("is_valid")
    private boolean isValid = true;
}
