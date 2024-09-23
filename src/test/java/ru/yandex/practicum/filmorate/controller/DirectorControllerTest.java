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
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DirectorController.class)
class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DirectorService directorService;

    private static ObjectMapper mapper;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addFailName() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": null}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \" \"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void addSuccess() throws Exception {

        var content = "{\"name\": \"director name\"}";
        var answer = "{\"id\": 1,\"name\": \"director name\"}";

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        when(directorService.add(mapper.readValue(content, Director.class)))
                .thenReturn(mapper.readValue(answer, Director.class));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateFailName() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": null}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"\"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \" \"}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void updateSuccess() throws Exception {

        var content = "{\"id\": 1,\"name\": \"director name\"}";
        var answer = "{\"id\": 1,\"name\": \"director name\"}";

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/directors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        when(directorService.update(mapper.readValue(content, Director.class)))
                .thenReturn(mapper.readValue(answer, Director.class));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("director name")));
    }

    @Test
    void deleteSuccess() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/directors/1");

        doNothing().when(directorService).delete(1);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNotFound() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/directors/-1");

        doThrow(new NotFoundException("director with id == -1 not found"))
                .when(directorService).delete(-1);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());
    }
}