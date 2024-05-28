package com.example.amazon.DTO.User;

import com.example.amazon.Model.Enums.GenderEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDTO {

    @Size(max=255, message="Name must not be longer than {max} characters.")
    @NotEmpty(message="Name cannot be empty.")
    private String name;

    @Past(message="Birthdate must be in the past.")
    @JsonProperty("birth_date")
    private Date birthDate;

    @JsonProperty("gender")
    private GenderEnum genderEnum;

    @Size(max=30, message="Mobile number must not be longer than {max} characters.")
    @NotEmpty(message="Mobile number cannot be empty.")
    private String mobile;
}
