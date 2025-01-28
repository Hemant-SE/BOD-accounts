package com.bod.accounts.repository;

import com.bod.accounts.AuditingTestConfig;
import com.bod.accounts.entity.Accounts;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


import java.util.Optional;
@DataJpaTest
@Import(AuditingTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountsRepositoryTests  {
    @Autowired
    private AccountsRepository accountsRepository;
    private Accounts account;
    @BeforeEach
    void setUp(){
        account = new Accounts();
        account.setCustomerId(1L);
        account.setAccountNumber(100L);
        account.setAccountType("Saving");
        account.setBranchAddress("Delhi");
    }
    @Test
    void save_should_persist_account() {
        // Given
        // When
        Accounts savedAccount = accountsRepository.save(account);
        // Then
        assertNotNull(savedAccount);
    }

    @Test
    void findById_should_return_account_when_exists() {
        // Given
        Accounts savedAccount = accountsRepository.save(account);
        // When
        Optional<Accounts> retrievedAccount = accountsRepository.findById(savedAccount.getAccountNumber());

        // Then
        assertTrue(retrievedAccount.isPresent());
    }

    @Test
    void findByIdCustomerId_should_return_account() {
        // Given
        Accounts savedAccount = accountsRepository.save(account);
        // When
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(savedAccount.getCustomerId());
        //then
        assertTrue(accounts.isPresent());
    }

    @Test
    void deleteById_should_remove_account() {
        // Given
        Accounts savedAccount = accountsRepository.save(account);
        // When
        accountsRepository.deleteById(savedAccount.getAccountNumber());
        Optional<Accounts> deletedAccount = accountsRepository.findById(savedAccount.getAccountNumber());
        // Then
        assertFalse(deletedAccount.isPresent());
    }
}
