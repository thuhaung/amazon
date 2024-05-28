package com.example.amazon.Controller.Payload.Request.User;

import com.example.amazon.Model.Enums.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGenderRequest {

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
}
