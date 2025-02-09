package com.siddhu.spring.customer;

import com.siddhu.spring.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.getCustomers();
    }
    @GetMapping("{id}")
    public CustomerDTO getCustomer(@PathVariable("id") Integer id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(
            @RequestBody CustomerRegistrationRequest request
    ) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.name(), "role_user");
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION,jwtToken)
                .build();
    }

    @DeleteMapping("{id}")
    public void removeCustomer(@PathVariable("id") Integer id) {
        customerService.removeCustomerById(id);
    }

    @PutMapping("{id}")
    public void updateCustomer(
            @PathVariable("id") Integer id,
            @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomer(id,customerUpdateRequest);
    }

    @PostMapping(
            value = "{id}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadCustomerProfileImage(
            @PathVariable("id") Integer id,
            @RequestParam("file") MultipartFile file
            ) {
        customerService.uploadCustomerProfileImage(id,file);
    }

    @GetMapping("{id}/profile-image")
    public byte[] getCustomerProfileImage(@PathVariable("id") Integer id) {
        return customerService.getCustomerProfileImage(id);
    }


}
