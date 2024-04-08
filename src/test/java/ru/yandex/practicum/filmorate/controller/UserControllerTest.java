package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static ObjectMapper mapper;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void postOk() throws Exception {
        var content = "{\"email\": \"user@mail.com\",\"login\": \"User_login\"}";
        var answer = "{\"email\": \"user@mail.com\",\"login\": \"User_login\",\"name\": \"User_login\"}";

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@mail.com\",\"login\": \"User_login\"}");
        when(userService.addUser(mapper.readValue(content, User.class)))
                .thenReturn(mapper.readValue(answer, User.class));
        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("User_login")));

        var date = LocalDate.now();

        content = "{\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                "\"name\": \"User name\",\"birthday\": \"" + date + "\"}";
        answer = "{\"id\": 2,\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                "\"name\": \"User name\",\"birthday\": \"" + date + "\"}";

        mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        when(userService.addUser(mapper.readValue(content, User.class)))
                .thenReturn(mapper.readValue(answer, User.class));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)));
    }

    @Test
    void postValidationFailEmail() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": null,\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"usermail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"usermail.com@\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void postValidationFailLogin() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@mail.com\",\"login\": null," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@mail.com\",\"login\": \"\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@mail.com\",\"login\": \"User login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void postValidationFailBirthday() throws Exception {
        var date = LocalDate.now().plusDays(1);
        var content = "{\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                "\"name\": \"User name\",\"birthday\": \"" + date + "\"}";

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void putOk() throws Exception {

        var date = LocalDate.now().toString();
        var content = "{\"id\": 1,\"email\": \"new@mail.com\",\"login\": \"NewLogin\"," +
                "\"birthday\": \"" + date + "\"}";
        var answer = "{\"id\": 1,\"email\": \"new@mail.com\",\"login\": \"NewLogin\"," +
                "\"name\": \"NewLogin\",\"birthday\": \"" + date + "\"}";
        var mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        when(userService.changeUser(mapper.readValue(content, User.class)))
                .thenReturn(mapper.readValue(answer, User.class));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("new@mail.com")))
                .andExpect(jsonPath("$.login", is("NewLogin")))
                .andExpect(jsonPath("$.name", is("NewLogin")))
                .andExpect(jsonPath("$.birthday", is(date)));
    }

    @Test
    void putValidationFailEmail() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@mail.com\",\"login\": \"User_login\"}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": null,\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"usermail.com\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"usermail.com@\",\"login\": \"User_login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void putValidationFailLogin() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@mail.com\",\"login\": \"User_login\"}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"user@mail.com\",\"login\": null," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"user@mail.com\",\"login\": \"\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"email\": \"user@mail.com\",\"login\": \"User login\"," +
                        "\"name\": \"User name\",\"birthday\": \"2005-05-15\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void putValidationFailBirthday() throws Exception {
        var date = LocalDate.now().plusDays(1);
        var content = "{\"id\": 1,\"email\": \"user@mail.com\",\"login\": \"User_login\"," +
                "\"name\": \"User name\",\"birthday\": \"" + date + "\"}";

        var mockRequest = MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }
}