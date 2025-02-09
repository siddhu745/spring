package com.siddhu.spring.customer;

import com.siddhu.spring.exceptions.DuplicateResourceException;
import com.siddhu.spring.exceptions.RequestValidationException;
import com.siddhu.spring.exceptions.ResourceNotFoundException;
import com.siddhu.spring.s3.S3Buckets;
import com.siddhu.spring.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;
    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Service s3Service;

    @Mock
    private S3Buckets s3Buckets;


    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(
                customerDao,
                customerDTOMapper,
                passwordEncoder,
                s3Service,
                s3Buckets
                );
    }

    @Test
    void getCustomers() {
        //When
        underTest.getCustomers();

        //Then
        verify(customerDao).getCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                id, "siddhu", "password", new Date(2002, 28, 12), "male"
        );
        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected  = customerDTOMapper.apply(customer);

        //When
        CustomerDTO actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowGetCustomerWhenReturnsEmptyOptional() {
        //Given
        int id = 10;
        when(customerDao.getCustomer(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id %s not found".formatted(id));

    }


    @Test
    void addCustomer() {
        //Given
        String name = "SiddhuBoy";
        when(customerDao.existsPersonWithName(name)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                "password", new Date(2002, 12, 28),
                "male"
        );

        String passwordHash = "&fhsk23lks90hhgy344";
        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();
        assertThat(value.getId()).isNull();
        assertThat(value.getName()).isEqualTo(request.name());
        assertThat(value.getPassword()).isEqualTo(passwordHash);
        assertThat(value.getDate()).isEqualTo(request.date());
        assertThat(value.getGender()).isEqualTo(request.gender());
    }

    @Test
    void willThrowWhenNameExistsWhileAddingCustomer() {
        //Given
        String name = "SiddhuBoy";
        when(customerDao.existsPersonWithName(name)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                "password", new Date(2002, 12, 28),
                "male"
        );

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("name already taken");

        //Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void removeCustomerById() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);

        //When
        underTest.removeCustomerById(id);

        //Then
        verify(customerDao).deleteCustomer(id);
    }

    @Test
    void willThrowWhenThereIsNoIdToRemoveCustomerById() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(() ->underTest.removeCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id %s not found".formatted(id));

        //Then
        verify(customerDao,never()).deleteCustomer(any());
    }


    @Test
    void canUpdateAllCustomerProperties() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "siddhuBoy",
                "password", new Date(2002, 12, 28),
                "male"
        );
        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));
        String name = "SiddhuBoyy";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                name,
                new Date(2000, 12, 28),
                "alpha male"
        );
        when(customerDao.existsPersonWithName(name)).thenReturn(false);

        //When
        underTest.updateCustomer(id, customerUpdateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();

        assertThat(value.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(value.getDate()).isEqualTo(customerUpdateRequest.date());
        assertThat(value.getGender()).isEqualTo(customerUpdateRequest.gender());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "siddhuBoy",
                "password", new Date(2002, 12, 28),
                "male"
        );
        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));
        String name = "SiddhuBoyy";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                name,
                null,
                null
        );
        when(customerDao.existsPersonWithName(name)).thenReturn(false);

        //When
        underTest.updateCustomer(id, customerUpdateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();

        assertThat(value.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(value.getDate()).isEqualTo(customer.getDate());
        assertThat(value.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void canUpdateOnlyCustomerDate() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "siddhuBoy",
                "password", new Date(2002, 12, 28),
                "male"
        );
        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                new Date(2000, 12, 28),
                null
        );

        //When
        underTest.updateCustomer(id, customerUpdateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();

        assertThat(value.getName()).isEqualTo(customer.getName());
        assertThat(value.getDate()).isEqualTo(customerUpdateRequest.date());
        assertThat(value.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void canUpdateOnlyCustomerGender() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "siddhuBoy",
                "password", new Date(2002, 12, 28),
                "male"
        );
        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                null,
                "alpha male"
        );

        //When
        underTest.updateCustomer(id, customerUpdateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();

        assertThat(value.getName()).isEqualTo(customer.getName());
        assertThat(value.getDate()).isEqualTo(customer.getDate());
        assertThat(value.getGender()).isEqualTo(customerUpdateRequest.gender());
    }

    @Test
    void canThrowWhenCustomerNameAlreadyExists() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "siddhuBoy",
                "password", new Date(2002, 12, 28),
                "male"
        );
        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));
        String name = "SiddhuBoyy";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                name,
                null,
                null
        );
        when(customerDao.existsPersonWithName(name)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("customer with %s already exists".formatted(customerUpdateRequest.name()));

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenNoChangesToUpdateCustomer() {
        //Given
        int id = 10;
        String name = "siddhuBoy";
        Date date = new Date(2002, 12, 28);
        String gender = "male";
        Customer customer = new Customer(
                id,
                name,
                "password", date,
                gender
        );
        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                name,
                date,
                gender
        );

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }




}