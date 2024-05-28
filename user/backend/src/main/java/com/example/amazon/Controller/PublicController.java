package com.example.amazon.Controller;

import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.Service.User.UserServiceImpl;
import com.example.amazon.Util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/poll")
public class PublicController {
    private final UserServiceImpl userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<CustomResponse> pollUserTransaction(
        @PathVariable Long id
    ) {
        userService.existsById(id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "User with id " + id + " exists."
        );
    }
}
