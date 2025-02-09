package com.siddhu.spring.customer;

import com.siddhu.spring.exceptions.DuplicateResourceException;
import com.siddhu.spring.exceptions.RequestValidationException;
import com.siddhu.spring.exceptions.ResourceNotFoundException;
import com.siddhu.spring.s3.S3Buckets;
import com.siddhu.spring.s3.S3Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;


    public CustomerService(
            @Qualifier("jdbc") CustomerDao customerDao,
            CustomerDTOMapper customerDTOMapper,
            PasswordEncoder passwordEncoder,
            S3Service s3Service,
            S3Buckets s3Buckets
    ) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    List<CustomerDTO> getCustomers() {
        return customerDao.getCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    CustomerDTO getCustomer(Integer id) {
        return customerDao.getCustomer(id)
                .map(customerDTOMapper)
                .orElseThrow(
                () -> new ResourceNotFoundException(
                        "Customer with id %s not found".formatted(id)
                )
        );
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        //check is email exists
        String name = request.name();
        if(customerDao.existsPersonWithName(name)){
            throw new DuplicateResourceException(
                    "name already taken"
            );
        }

        Customer customer = new Customer(
                request.name(),
                passwordEncoder.encode(request.password()),
                request.date(),
                request.gender()
        );

        //add new customer
       customerDao.insertCustomer(customer);
    }

    public void removeCustomerById(Integer id) {
        checkIfCustomerExistsOrThrow(id);
        customerDao.deleteCustomer(id);
    }

    private void checkIfCustomerExistsOrThrow(Integer id) {
        if(!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException(
                    "customer with id %s not found".formatted(id)
            );

        }
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = customerDao.getCustomer(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Customer with id %s not found".formatted(id)
                        )
                );
        boolean changes = false;

        if(customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())){
            if(customerDao.existsPersonWithName(customerUpdateRequest.name())) {
                throw new DuplicateResourceException(
                        "customer with %s already exists".formatted(customerUpdateRequest.name())
                );
            }
            customer.setName(customerUpdateRequest.name());
            changes = true;
        }

        if(customerUpdateRequest.date() != null && !customerUpdateRequest.date().toString().equals(customer.getDate().toString())){
            customer.setDate(customerUpdateRequest.date());
            changes = true;
        }

        if(customerUpdateRequest.gender() != null && !customerUpdateRequest.gender().equals(customer.getGender())){
            customer.setGender(customerUpdateRequest.gender());
            changes = true;
        }
        if(!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);

    }

    public void uploadCustomerProfileImage(Integer id, MultipartFile file) {
        checkIfCustomerExistsOrThrow(id);
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-image/%s/%s".formatted(id,profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        customerDao.updateCustomerProfileImageId(profileImageId,id);
    }

    public byte[] getCustomerProfileImage(Integer id) {
        CustomerDTO customer = customerDao.getCustomer(id)
                .map(customerDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Customer with id %s not found".formatted(id)
                        )
                );
        // TODO: check if profile image id is empty or null
        var profileImageId = customer.profileImageId();
        if(customer.profileImageId().isBlank()) {
            throw new ResourceNotFoundException(
                    "customer with id %s profile image id not found".formatted(id)
            );
        }

        byte[] profileImage = s3Service.getObject(
                s3Buckets.getCustomer(),
                "profile-image/%s/%s".formatted(id, profileImageId)
                );
        return profileImage;
    }
}
