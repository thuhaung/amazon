package com.example.amazon.Repository;

import com.example.amazon.DTO.Address.AddressDTO;
import com.example.amazon.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT new com.example.amazon.DTO.Address.AddressDTO(x.building, x.street, x.city, x.region, x.country, x.postalCode, x.addressType, x.isDefault) FROM Address x WHERE x.id =?1")
    List<AddressDTO> findByUserId(Long userId);
    @Query("UPDATE Address x SET x.isDefault = false WHERE x.isDefault = true AND x.user.id = ?1")
    @Modifying
    void removePreviousDefaultAddressSetting(Long userId);
}
