package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void postOk() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        mockRequest = MockMvcRequestBuilders.get("/user");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("User_login")));

        mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"" + LocalDate.now() + "\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

    }

    @Test
    void postValidationFailEmail() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": null,\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"usermail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"usermail.com@\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));
    }

    @Test
    void postValidationFailLogin() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": null," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));
    }

    @Test
    void postValidationFailBirthday() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"" + LocalDate.now().plusDays(1) + "\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));
    }

    @Test
    void putOk() throws Exception {

        var date = LocalDate.now().toString();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\"}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"new@mail.com\",\"login\": \"NewLogin\"," +
                        "\"birthday\": \"" + date + "\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        mockRequest = MockMvcRequestBuilders.get("/user");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is("new@mail.com")))
                .andExpect(jsonPath("$[0].login", is("NewLogin")))
                .andExpect(jsonPath("$[0].name", is("NewLogin")))
                .andExpect(jsonPath("$[0].birthday", is(date)));

    }

    @Test
    void putValidationFailEmail() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": null,\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"usermail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"usermail.com@\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));
    }

    @Test
    void putValidationFailLogin() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": null," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));
    }

    @Test
    void putValidationFailBirthday() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"" + LocalDate.now().plusDays(1) + "\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Validation exception")));
    }

    @Test
    void putNotFound() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 0,\"email\": \"user@mail.com\",\"login\": \"User_login\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());
    }
}