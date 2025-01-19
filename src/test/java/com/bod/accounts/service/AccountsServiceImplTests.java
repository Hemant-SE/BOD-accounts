package com.bod.accounts.service;

import com.bod.accounts.dto.AccountsDto;
import com.bod.accounts.dto.CustomerDto;
import com.bod.accounts.entity.Accounts;
import com.bod.accounts.entity.Customer;
import com.bod.accounts.exception.CustomerAlreadyExistsException;
import com.bod.accounts.exception.ResourceNotFoundException;
import com.bod.accounts.repository.AccountsRepository;
import com.bod.accounts.repository.CustomerRepository;
import com.bod.accounts.service.impl.AccountsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountsServiceImplTests {
    @Mock
    private AccountsRepository accountsRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private AccountsServiceImpl accountsService;

    private CustomerDto customerDto;
    private Customer customer;
    private AccountsDto accountsDto;
    private Accounts accounts;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
        customerDto.setName("Hemant");
        customerDto.setEmail("hemantraghav@gmail.com");
        customerDto.setMobileNumber("9278091701");

        customer = new Customer();
        customer.setName("Hemant");
        customer.setEmail("hemantraghav@gmail.com");
        customer.setMobileNumber("9278091701");

        accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(1191861191L);
        accountsDto.setAccountType("Savings");
        accountsDto.setBranchAddress("Bank of Delhi, Connaught place new delhi");

        accounts = new Accounts();
        accounts.setAccountNumber(1191861191L);
        accounts.setAccountType("Savings");
        accounts.setBranchAddress("Bank of Delhi, Connaught place new delhi");
    }

    @Test
    void testCreateAccount_CustomerNotExists() {
        // Arrange
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        accountsService.createAccount(customerDto);

        // Assert
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(accountsRepository, times(1)).save(any(Accounts.class));
    }

    @Test
    void testCreateAccount_CustomerAlreadyExists() {
        // Arrange
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(CustomerAlreadyExistsException.class, () -> accountsService.createAccount(customerDto));
        verify(customerRepository, times(1)).findByMobileNumber(anyString());
        verify(accountsRepository, times(0)).save(any(Accounts.class));
    }

    @Test
    void testFetchAccount() {
        // Arrange
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(customer));
        when(accountsRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(accounts));

        // Act
        CustomerDto result = accountsService.fetchAccount("1234567890");

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("SAVINGS", result.getAccountsDto().getAccountType());
        verify(customerRepository, times(1)).findByMobileNumber(anyString());
        verify(accountsRepository, times(1)).findByCustomerId(anyLong());
    }

    @Test
    void testFetchAccount_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountsService.fetchAccount("1234567890"));
        verify(customerRepository, times(1)).findByMobileNumber(anyString());
        verify(accountsRepository, times(0)).findByCustomerId(anyLong());
    }

    @Test
    void testUpdateAccount_ValidUpdate() {
        // Arrange
        AccountsDto updatedAccountsDto = new AccountsDto(1191861191L, "CURRENT", "New Address");
        customerDto.setAccountsDto(updatedAccountsDto);
        when(accountsRepository.findById(anyLong())).thenReturn(Optional.of(accounts));
        when(accountsRepository.save(any(Accounts.class))).thenReturn(accounts);
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        boolean result = accountsService.updateAccount(customerDto);

        // Assert
        assertTrue(result);
        verify(accountsRepository, times(1)).save(any(Accounts.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateAccount_AccountNotFound() {
        // Arrange
        AccountsDto updatedAccountsDto = new AccountsDto(1000000000L, "CURRENT", "New Address");
        customerDto.setAccountsDto(updatedAccountsDto);
        when(accountsRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountsService.updateAccount(customerDto));
        verify(accountsRepository, times(1)).findById(anyLong());
        verify(customerRepository, times(0)).save(any(Customer.class)); // Account not updated
    }

    @Test
    void testDeleteAccount_Success() {
        // Arrange
        // Mock that the customer is found by the mobile number
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(customer));
        doNothing().when(accountsRepository).deleteByCustomerId(anyLong());  // Mock void delete method
        doNothing().when(customerRepository).deleteById(anyLong());  // Mock void delete method

        // Act
        boolean result = accountsService.deleteAccount("1234567890");

        // Assert
        assertTrue(result);  // Ensure the result is true if deletion is successful
        verify(customerRepository, times(1)).findByMobileNumber(anyString());  // Verify findByMobileNumber was called
        verify(accountsRepository, times(1)).deleteByCustomerId(anyLong());  // Verify deleteByCustomerId was called
        verify(customerRepository, times(1)).deleteById(anyLong());  // Verify deleteById was called
    }

    @Test
    void testDeleteAccount_CustomerNotFound() {
        // Arrange
        // Mock that the customer is not found by the mobile number
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        // Verify that a ResourceNotFoundException is thrown when the customer is not found
        assertThrows(ResourceNotFoundException.class, () -> accountsService.deleteAccount("1234567890"));

        // Verify that deleteByCustomerId and deleteById were not called
        verify(customerRepository, times(1)).findByMobileNumber(anyString());  // Verify findByMobileNumber was called
        verify(accountsRepository, times(0)).deleteByCustomerId(anyLong());  // Verify deleteByCustomerId was not called
        verify(customerRepository, times(0)).deleteById(anyLong());  // Verify deleteById was not called
    }
}
