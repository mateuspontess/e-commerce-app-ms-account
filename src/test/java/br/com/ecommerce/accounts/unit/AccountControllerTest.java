package br.com.ecommerce.accounts.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.accounts.model.AddressDTO;
import br.com.ecommerce.accounts.model.LoginDTO;
import br.com.ecommerce.accounts.model.UserClientDTO;
import br.com.ecommerce.accounts.model.UserEmployeeDTO;
import br.com.ecommerce.accounts.repository.UserRepository;
import br.com.ecommerce.accounts.service.UserService;

@WebMvcTest
@AutoConfigureJsonTesters
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<LoginDTO> loginDTOJson;
    @Autowired
    private JacksonTester<UserClientDTO> userClientDTOJson;
    @Autowired
    private JacksonTester<UserEmployeeDTO> userEmployeeDTOJson;


    @MockBean
    private UserService service;
    @MockBean
    private UserRepository repository;


    @Test
    @DisplayName("Login unit test - must return status 200")
    void loginTest01() throws IOException, Exception {
        // arrange
        String USERNAME = "username";
        String PASSWORD = "password@123";

        // act
        mvc.perform(
                post("/account/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson.write(new LoginDTO(USERNAME, PASSWORD)).getJson()))
                .andExpect(status().isOk());

        verify(service).auth(any());
    }
    @Test
    @DisplayName("Login units test with invalid data - must return status 400")
    void loginTest02() throws IOException, Exception {
        // arrange
        String USERNAME = "us";
        String PASSWORD = "password";

        // act
        mvc.perform(
                post("/account/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson.write(new LoginDTO(USERNAME, PASSWORD)).getJson()))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("Create client user unit test - must return status 200 and user data")
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

        // act and assert
        mvc.perform(
                post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userClientDTOJson.write(userClientDTO).getJson()))
                .andExpect(status().isOk());

        verify(service).createUserClient(any());
    }
    @Test
    @DisplayName("Create client user uni test with invalid data - must return status 400 and fields with error")
    void createClientUserTest02() throws IOException, Exception {
        // arrange
        UserClientDTO userClientDTO = new UserClientDTO(
                "us",
                "password",
                "",
                "clientemail.com",
                "(11) 9999-9999",
                "524.323.760-40",
                new AddressDTO(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""));

        // act and assert
        mvc.perform(
                post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userClientDTOJson.write(userClientDTO).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create employee user unit test - must return status 200 and user data")
    void createEmployeeUserTest01() throws IOException, Exception {
        // arrange
        UserEmployeeDTO userEmployee = new UserEmployeeDTO(
            "userclient-san",
            "password-san!@123",
            "Client-san");

        // act and assert
        mvc.perform(
                post("/account/create/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userEmployeeDTOJson.write(userEmployee).getJson()))
                .andExpect(status().isOk());

        verify(service).createUserEmployee(any());
    }

    @Test
    @DisplayName("Create employee user unit test with invalid data - must return status 400 and fields with error")
    void createEmployeeUserTest02() throws IOException, Exception {
        // arrange
        UserEmployeeDTO userEmployee = new UserEmployeeDTO(
            "",
            "",
            "");

        // act and assert
        mvc.perform(
                post("/account/create/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userEmployeeDTOJson.write(userEmployee).getJson()))
                .andExpect(status().isBadRequest());
    }
}