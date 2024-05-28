package com.example.amazon.Controller;

import com.example.amazon.Controller.Payload.Request.User.UpdateBirthDateRequest;
import com.example.amazon.Controller.Payload.Request.User.UpdateGenderRequest;
import com.example.amazon.Controller.Payload.Request.User.UpdateMobileRequest;
import com.example.amazon.Controller.Payload.Request.User.UpdateNameRequest;
import com.example.amazon.DTO.Address.AddressDTO;
import com.example.amazon.DTO.BankAccount.BankAccountDTO;
import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.Service.Address.AddressServiceImpl;
import com.example.amazon.Service.BankAccount.BankAccountServiceImpl;
import com.example.amazon.Service.User.UserServiceImpl;
import com.example.amazon.Util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@PreAuthorize("@authUtil.isAuthorized(#authId, #id)")
public class UserController {

    private final UserServiceImpl userService;
    private final AddressServiceImpl addressService;
    private final BankAccountServiceImpl bankAccountService;

    // USER INFO
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> getUser(
        @PathVariable Long id,
        @RequestHeader("authId") String authId
    ) {
        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            userService.getUserDTOById(id)
        );
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<CustomResponse> updateName(
        @PathVariable Long id,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody UpdateNameRequest request
    ) {
        userService.updateName(id, request.getName());

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Name updated successfully."
        );
    }

    @PatchMapping("/{id}/gender")
    public ResponseEntity<CustomResponse> updateGender(
        @PathVariable Long id,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody UpdateGenderRequest request
    ) {
        userService.updateGender(id, request.getGender());

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Gender updated successfully."
        );
    }

    @PatchMapping("/{id}/birthdate")
    public ResponseEntity<CustomResponse> updateBirthDate(
        @PathVariable Long id,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody UpdateBirthDateRequest request
    ) {
        userService.updateBirthDate(id, request.getBirthDate());

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Birthdate updated successfully."
        );
    }

    @PatchMapping("/{id}/mobile")
    public ResponseEntity<CustomResponse> updateMobileNumber(
        @PathVariable Long id,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody UpdateMobileRequest request
    ) {
        userService.updateMobile(id, request.getMobile());

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Phone number updated successfully."
        );
    }


    // BANK ACCOUNT
    @GetMapping("/{id}/bank-account")
    public ResponseEntity<CustomResponse> getBankAccounts(
        @PathVariable Long id,
        @RequestHeader("authId") String authId
    ) {
        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            bankAccountService.getBankAccountsByUserId(id)
        );
    }

    @PostMapping("/{id}/bank-account")
    public ResponseEntity<CustomResponse> addBankAccount(
        @PathVariable Long id,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody BankAccountDTO request
    ) {
        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Bank account added successfully.",
            bankAccountService.addBankAccount(request, id)
        );
    }

    @PutMapping("/{id}/bank-account/{bankAccountId}")
    public ResponseEntity<CustomResponse> updateBankAccount(
        @PathVariable Long id,
        @PathVariable Long bankAccountId,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody BankAccountDTO request
    ) {
        bankAccountService.updateBankAccountById(request, bankAccountId, id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Bank account updated successfully."
        );
    }

    @DeleteMapping("/{id}/bank-account/{bankAccountId}")
    public ResponseEntity<CustomResponse> removeBankAccount(
            @PathVariable Long id,
            @PathVariable Long bankAccountId,
            @RequestHeader("authId") String authId,
            @Valid @RequestBody BankAccountDTO request
    ) {
        bankAccountService.deleteBankAccountById(bankAccountId);

        return ResponseHandler.generateResponse(
                HttpStatus.OK,
                "Bank account removed successfully."
        );
    }


    // ADDRESS
    @GetMapping("/{id}/address")
    public ResponseEntity<CustomResponse> getAddresses(
        @PathVariable Long id,
        @RequestHeader("authId") String authId
    ) {
        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            addressService.getAddressesByUserId(id)
        );
    }

    @PostMapping("/{id}/address")
    public ResponseEntity<CustomResponse> addAddress(
        @PathVariable Long id,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody AddressDTO request
    ) {
        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Address added successfully.",
            addressService.addAddress(request, id)
        );
    }

    @PutMapping("/{id}/address/{addressId}")
    public ResponseEntity<CustomResponse> updateAddress(
        @PathVariable Long id,
        @PathVariable Long addressId,
        @RequestHeader("authId") String authId,
        @Valid @RequestBody AddressDTO request
    ) {
        addressService.updateAddressById(request, addressId, id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Address updated successfully."
        );
    }

    @DeleteMapping("{id}/address/{addressId}")
    public ResponseEntity<CustomResponse> removeAddress(
        @PathVariable Long id,
        @PathVariable Long addressId,
        @RequestHeader("authId") String authId
    ) {
        addressService.deleteAddressById(addressId);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Address removed successfully."
        );
    }
}
