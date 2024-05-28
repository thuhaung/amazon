package com.example.amazon.DTO.Address;

import com.example.amazon.Model.Enums.AddressEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {
    @Size(max=255, message="Building name must not be longer than {max} characters.")
    private String building;

    @Size(max=255, message="Street name must not be longer than {max} characters.")
    private String street;

    @Size(max=255, message="City name must not be longer than {max} characters.")
    @NotEmpty(message="City must not be empty.")
    private String city;

    @Size(max=255, message="Region name must not be longer than {max} characters.")
    @NotEmpty(message="Region/state must not be empty.")
    private String region;

    @Size(max=255, message="Country name must not be longer than {max} characters.")
    @NotEmpty(message="Country must not be empty.")
    private String country;

    @JsonProperty("postal_code")
    @Size(max=5, message="Postal code cannot be longer than {max} characters.")
    private String postalCode;

    @JsonProperty("address_type")
    private AddressEnum addressType;

    @JsonProperty("is_default")
    private boolean isDefault;
}
