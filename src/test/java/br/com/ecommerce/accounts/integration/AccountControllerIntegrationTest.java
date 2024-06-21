package br.com.ecommerce.accounts.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.accounts.dto.AddressDTO;
import br.com.ecommerce.accounts.dto.LoginDTO;
import br.com.ecommerce.accounts.dto.UserClientDTO;
import br.com.ecommerce.accounts.dto.UserEmployeeDTO;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.repository.UserRepository;
import br.com.ecommerce.accounts.utils.TokenFormatValidatorUtils;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository repository;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JacksonTester<LoginDTO> loginDTOJson;
    @Autowired
    private JacksonTester<UserClientDTO> userClientDTOJson;
    @Autowired
    private JacksonTester<UserEmployeeDTO> userEmployeeDTOJson;


    @Test
    @DisplayName("Integration - login - Must return status 200 and a valid token")
    void loginTest01() throws IOException, Exception {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "test@123";
        User user = User.builder().username(USERNAME).password(encoder.encode(PASSWORD)).build();
        repository.save(user);

        LoginDTO requestBody = new LoginDTO(USERNAME, PASSWORD);

        // act
        var result = mvc.perform(
            post("/account/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn().getResponse();

        String responseBody = result.getContentAsString().replaceAll(".*\"token\":\\s*\"(.*?)\".*", "$1");

        // assert
        assertTrue(TokenFormatValidatorUtils.isValidTokenFormat(responseBody));
    }
    @Test
    @DisplayName("Integration - login - Should return status 400 when username and password do not meet the expected format")
    void loginTest02() throws IOException, Exception {
        // arrange
        String USERNAME = "te";
        String PASSWORD = "te";
        User user = User.builder().username(USERNAME).password(encoder.encode(PASSWORD)).build();
        repository.save(user);

        LoginDTO requestBody = new LoginDTO(USERNAME, PASSWORD);

        // act
        mvc.perform(
            post("/account/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.token").doesNotExist())
        .andExpect(jsonPath("$.fields.username").exists())
        .andExpect(jsonPath("$.fields.password").exists());
    }
    @Test
    @DisplayName("Integration - login - Should return status 400 when user is not found")
    void loginTest03() throws IOException, Exception {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "test@123";
        User user = User.builder().username(USERNAME).password(encoder.encode(PASSWORD)).build();
        repository.save(user);
        
        String INVALID_USERNAME = "non-existent";
        LoginDTO requestBody = new LoginDTO(INVALID_USERNAME, PASSWORD);

        // act
        mvc.perform(
            post("/account/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.token").doesNotExist());
    }
    @Test
    @DisplayName("Integration - login - Should return 401 status when password does not match")
    void loginTest04() throws IOException, Exception {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "test@123";
        User user = User.builder().username(USERNAME).password(encoder.encode(PASSWORD)).build();
        repository.save(user);
        
        LoginDTO requestBody = new LoginDTO(USERNAME, PASSWORD + "invalidatingPassword");

        // act
        mvc.perform(
            post("/account/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @DisplayName("Integration - createClientUser - Must return status 200 and user data")
    void createClientUserTest01() throws IOException, Exception {
        // arrange
        UserClientDTO userClientDTO = new UserClientDTO(
            "userclient-san",
            "password-san!@123",
            "Client-san",
            "client@email.com",
            "(11) 99999-9999",
            "524.323.760-48",
            new AddressDTO(
                "street",
                "neighborhood",
                "postal_code",
                "number",
                "complement",
                "city",
                "state"));

        // act
        mvc.perform(
            post("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userClientDTOJson.write(userClientDTO).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").isNotEmpty())
        .andExpect(jsonPath("$.email").isNotEmpty())
        .andExpect(jsonPath("$.phone_number").isNotEmpty())
        .andExpect(jsonPath("$.cpf").isNotEmpty());
    }
    @Test
    @DisplayName("Integration - createClientUser with invalid data - Must return status 400 and fields with error")
    void createClientUserTest02() throws IOException, Exception {
        // arrange
        UserClientDTO userClientDTO = new UserClientDTO(
            "us",
            "password", // invalid format
            "",
            "clientemail.com", // invalid format
            "(11) 9999-9999", // invalid format
            "524.323.760-40", // invalid format
            new AddressDTO(
                "",
                "",
                "",
                "",
                "",
                "",
                ""));

        // act
        mvc.perform(
            post("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userClientDTOJson.write(userClientDTO).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.username").exists())
        .andExpect(jsonPath("$.fields.password").exists())
        .andExpect(jsonPath("$.fields.name").exists())
        .andExpect(jsonPath("$.fields.email").exists())
        .andExpect(jsonPath("$.fields.phone_number").exists())
        .andExpect(jsonPath("$.fields.['address.street']").exists())
        .andExpect(jsonPath("$.fields.['address.neighborhood']").exists())
        .andExpect(jsonPath("$.fields.['address.postal_code']").exists())
        .andExpect(jsonPath("$.fields.['address.number']").exists())
        .andExpect(jsonPath("$.fields.['address.complement']").exists())
        .andExpect(jsonPath("$.fields.['address.city']").exists())
        .andExpect(jsonPath("$.fields.['address.state']").exists());
    }

    @Test
    @DisplayName("Integration - createEmployee - must return status 200 and user data")
    void createEmployeeUserTest01() throws IOException, Exception {
        // arrange
        UserEmployeeDTO userEmployee = new UserEmployeeDTO(
            "userclient-san",
            "password-san!@123",
            "Client-san");

        // act
        mvc.perform(
            post("/account/create/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userEmployeeDTOJson.write(userEmployee).getJson())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.username").isNotEmpty())
        .andExpect(jsonPath("$.name").isNotEmpty());
    }
    @Test
    @DisplayName("Integration - createEmployee - Must return status 400 and fields with error")
    void createEmployeeUserTest02() throws IOException, Exception {
        // arrange
        UserEmployeeDTO userEmployee = new UserEmployeeDTO(
            "ab",
            "password",
            "");

        // act
        mvc.perform(
            post("/account/create/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userEmployeeDTOJson.write(userEmployee).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.username").exists())
        .andExpect(jsonPath("$.fields.password").exists())
        .andExpect(jsonPath("$.fields.name").exists());
    }
}