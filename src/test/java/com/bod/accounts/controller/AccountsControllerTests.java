package com.bod.accounts.controller;

import com.bod.accounts.constants.AccountsConstants;
import com.bod.accounts.dto.AccountsDto;
import com.bod.accounts.dto.CustomerDto;
import com.bod.accounts.exception.CustomerAlreadyExistsException;
import com.bod.accounts.service.IAccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountsControllerTests {

    @MockitoBean
    private IAccountsService iAccountsService;

    @Autowired
    private MockMvc mockMvc;

    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        // Create and set up CustomerDto with AccountsDto
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(1191861191L);
        accountsDto.setAccountType(AccountsConstants.SAVINGS);
        accountsDto.setBranchAddress(AccountsConstants.ADDRESS);

        customerDto = new CustomerDto();
        customerDto.setName("Hemant");
        customerDto.setEmail("hemantraghav@gmail.com");
        customerDto.setMobileNumber("9278091701");
        customerDto.setAccountsDto(accountsDto);
    }

    @Test
    void testCreateAccount_success() throws Exception {
        //given part
        doNothing().when(iAccountsService).createAccount(any(CustomerDto.class));
        //When and then part
        mockMvc.perform(post("/api/accounts/create")
                        .contentType("application/json")
                        .content("{\"name\":\"Hemant\",\"email\":\"hemantraghav@gmail.com\",\"mobileNumber\":\"9278091701\",\"accountsDto\":{\"accountNumber\":1191861191,\"accountType\":\"Saving\",\"branchAddress\":\"Bank of Delhi, Connaught place, New Delhi\"}}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{\"statusCode\":\"201\",\"statusMsg\":\"Account created successfully\"}"));
    }
    @Test
    void testCreateAccount_failure() throws Exception {
        //given part
        doThrow(new CustomerAlreadyExistsException("Customer already registered with given mobileNumber " + customerDto.getMobileNumber())).when(iAccountsService).createAccount(any(CustomerDto.class));
        //When and then part
        mockMvc.perform(post("/api/accounts/create")
                        .contentType("application/json")
                        .content("{\"name\":\"Hemant\",\"email\":\"hemantraghav@gmail.com\",\"mobileNumber\":\"9278091701\",\"accountsDto\":{\"accountNumber\":1191861191,\"accountType\":\"Saving\",\"branchAddress\":\"Bank of Delhi, Connaught place, New Delhi\"}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFetchAccountDetails_Success() throws Exception {

        when(iAccountsService.fetchAccount("9278091701")).thenReturn(customerDto);

        // Perform the GET request to /fetch with the mobileNumber parameter
        mockMvc.perform(get("/api/accounts/fetch")
                        .param("mobileNumber", "9278091701"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{"
                        + "\"name\":\"Hemant\","
                        + "\"email\":\"hemantraghav@gmail.com\","
                        + "\"mobileNumber\":\"9278091701\","
                        + "\"accountsDto\":{"
                        + "\"accountNumber\":1191861191,"
                        + "\"accountType\":\"Saving\","
                        + "\"branchAddress\":\"Bank of Delhi, Connaught place, New Delhi\""
                        + "}"
                        + "}"));
    }

    @Test
    void testUpdateAccountDetails_success() throws Exception{
        when(iAccountsService.updateAccount(any())).thenReturn(true);

        mockMvc.perform(put("/api/accounts/update")
                        .contentType("application/json")
                        .content("{"
                                + "\"name\":\"Hemant\","
                                + "\"email\":\"hemantraghav@gmail.com\","
                                + "\"mobileNumber\":\"9278091701\","
                                + "\"accountsDto\":{"
                                + "\"accountNumber\":1191861191,"
                                + "\"accountType\":\"Current\","
                                + "\"branchAddress\":\"Bank of Delhi, Connaught place, New Delhi\""
                                + "}"
                                + "}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testUpdateAccountDetails_failure() throws Exception{

        when(iAccountsService.updateAccount(any())).thenReturn(false);

        mockMvc.perform(put("/api/accounts/update")
                        .contentType("application/json")
                        .content("{"
                                + "\"name\":\"Hemant\","
                                + "\"email\":\"hemantraghav@gmail.com\","
                                + "\"mobileNumber\":\"9278091701\","
                                + "\"accountsDto\":{"
                                + "\"accountNumber\":11918611911,"
                                + "\"accountType\":\"Current\","
                                + "\"branchAddress\":\"Bank of Delhi, Connaught place, New Delhi\""
                                + "}"
                                + "}")
                )
                .andExpect(status().isExpectationFailed())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testDeleteAccountDetails_success() throws Exception{

        when(iAccountsService.deleteAccount(customerDto.getMobileNumber())).thenReturn(true);

        mockMvc.perform(delete("/api/accounts/delete")
                        .param("mobileNumber",customerDto.getMobileNumber())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
    @Test
    void testDeleteAccountDetails_failure() throws Exception{
        String wrongMobileNumber = "9278091702";
        when(iAccountsService.deleteAccount(wrongMobileNumber)).thenReturn(false);

        mockMvc.perform(delete("/api/accounts/delete")
                        .param("mobileNumber",wrongMobileNumber)
                )
                .andExpect(status().isExpectationFailed())
                .andExpect(content().contentType("application/json"));
    }

}

