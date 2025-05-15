package vn.dk.BackendFoodApp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.address.AddressRequest;
import vn.dk.BackendFoodApp.dto.response.address.AddressResponse;
import vn.dk.BackendFoodApp.model.Address;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.repository.AddressRepository;
import vn.dk.BackendFoodApp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public void addAddress(AddressRequest request, String username) {
        User user = userRepository.findByUsername(username);

        if (request.getIsDefault()) {
            // Set all existing addresses of the user to isDefault = false
            List<Address> existingAddresses = addressRepository.findByUser(user);
            for (Address addr : existingAddresses) {
                if (addr.getIsDefault()) {
                    addr.setIsDefault(false);
                }
            }
            addressRepository.saveAll(existingAddresses);
        }

        Address newAddress = Address.builder()
                .address(request.getAddress())
                .addressType(request.getAddressType())
                .isDefault(request.getIsDefault())
                .user(user)
                .build();

        addressRepository.save(newAddress);
    }
    public List<AddressResponse> getAddressesByUsername(String username) {
        User user = userRepository.findByUsername(username);

        List<Address> addressList = addressRepository.findByUser(user);

        return addressList.stream()
                .map(address -> AddressResponse.builder()
                        .id(address.getId())
                        .address(address.getAddress())
                        .addressType(address.getAddressType())
                        .isDefault(address.getIsDefault())
                        .build())
                .collect(Collectors.toList());
    }
    public AddressResponse getDefaultAddressByUsername(String username) {
        Address defaultAddress = addressRepository.findByUserUsernameAndIsDefaultTrue(username);
        if (defaultAddress != null) {
            return AddressResponse.fromEntity(defaultAddress);
        }
        return null;
    }

    @Transactional
    public void deleteAddress(Long addressId, String username) {
        User user = userRepository.findByUsername(username);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        // Ensure the address belongs to the current user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Address does not belong to user");
        }

        addressRepository.delete(address);
    }
}
