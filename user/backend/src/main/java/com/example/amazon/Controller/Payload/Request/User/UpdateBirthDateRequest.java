package com.example.amazon.Controller.Payload.Request.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBirthDateRequest {
    @Past(message="Birth date must be in the past.")
    @JsonProperty("birth_date")
    private Date birthDate;
}
