package com.example.amazon.Service.Address;

import com.example.amazon.DTO.Address.AddressDTO;
import com.example.amazon.Model.Address;

import java.util.List;

public interface AddressService {
    Address getAddressById(Long addressId);
    List<AddressDTO> getAddressesByUserId(Long userId);
    AddressDTO addAddress(AddressDTO request, Long userId);
    int updateAddressById(AddressDTO request, Long addressId, Long userId);
    int deleteAddressById(Long addressId);
}
