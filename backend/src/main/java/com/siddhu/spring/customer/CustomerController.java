package com.siddhu.spring.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }
    @GetMapping("{id}")
    public Customer getCustomer(@PathVariable("id") Integer id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
    }

    @DeleteMapping("{id}")
    public void removeCustomer(@PathVariable("id") Integer id) {
        customerService.removeCustomerById(id);
    }

    @PutMapping("{id}")
    public void updateCustomer(
            @PathVariable Integer id,
            @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomer(id,customerUpdateRequest);
    }
}
