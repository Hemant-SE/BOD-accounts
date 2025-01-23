package com.bod.accounts.repository;

import com.bod.accounts.AuditingTestConfig;
import com.bod.accounts.entity.Accounts;
import com.bod.accounts.entity.Customer;
import com.bod.accounts.service.IAccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(AuditingTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryTests
{
    @Autowired
    private CustomerRepository customerRepository;
    private Customer customer;
    @BeforeEach
    void setUp(){
        customer = new Customer();
        customer.setName("Hemant raghav");
        customer.setMobileNumber("8378387204");
        customer.setEmail("hemant@gmail.com");
    }
    @Test
    void save_should_persist_customerDetails() {
        // Given
        // When
        Customer savedCustomerDetails = customerRepository.save(customer);
        // Then
        assertNotNull(savedCustomerDetails);
    }
    @Test
    void findById_should_return_customer_when_exists() {
        // Given
        Customer savedCustomerDetails = customerRepository.save(customer);
        // When
        Optional<Customer> retrievedCustomer = customerRepository.findById(savedCustomerDetails.getCustomerId());
        // Then
        assertTrue(retrievedCustomer.isPresent());
    }
    @Test
    void findByMobileNumber_should_return_customer_when_exists(){
        // Given
        Customer savedCustomerDetails = customerRepository.save(customer);
        // When
        Optional<Customer> retrievedCustomer = customerRepository.findByMobileNumber(savedCustomerDetails.getMobileNumber());
        // Then
        assertTrue(retrievedCustomer.isPresent());
    }
    @Test
    void DeleteById_should_delete_customer(){
        // Given
        Customer savedCustomerDetails = customerRepository.save(customer);
        //when
        customerRepository.deleteById(savedCustomerDetails.getCustomerId());
        //then
        Optional<Customer> deletedCustomer = customerRepository.findById(savedCustomerDetails.getCustomerId());
        assertFalse(deletedCustomer.isPresent());
    }
}
