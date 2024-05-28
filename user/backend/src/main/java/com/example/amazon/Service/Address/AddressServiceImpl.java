package com.example.amazon.Service.Address;

import com.example.amazon.DTO.Address.AddressDTO;
import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Model.Address;
import com.example.amazon.Model.User;
import com.example.amazon.Repository.AddressRepository;
import com.example.amazon.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(
            () -> new ResourceNotFoundException(
                "No address can be found with id " + addressId + "."
            )
        );
    }

    @Override
    public List<AddressDTO> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public AddressDTO addAddress(AddressDTO request, Long userId) {
        if (userRepository.existsById(userId)) {
            User user = userRepository.getReferenceById(userId);

            Address address = modelMapper.map(request, Address.class);
            address.setUser(user);

            if (request.isDefault()) {
                addressRepository.removePreviousDefaultAddressSetting(userId);
            }

            addressRepository.save(address);

            return request;
        }

        throw new ResourceNotFoundException("No user can be found with id " + userId + ".");
    }

    @Override
    @Transactional
    public int updateAddressById(AddressDTO request, Long addressId, Long userId) {
        Address address = getAddressById(addressId);

        if (request.isDefault()) {
            addressRepository.removePreviousDefaultAddressSetting(userId);
        }

        modelMapper.map(request, address);

        addressRepository.save(address);

        return 1;
    }

    @Override
    @Transactional
    public int deleteAddressById(Long addressId) {
        addressRepository.deleteById(addressId);

        return 1;
    }
}
