package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postValidationFailName() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": null,\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-29\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-29\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void postValidationFailDescription() throws Exception {
        var longDescription = "d".repeat(201);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"description\": \"" + longDescription + "\"," +
                        "\"releaseDate\": \"1895-12-29\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void postValidationFailReleaseDate() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-27\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void postValidationFailDuration() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-28\",\"duration\": 0}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-28\",\"duration\": -1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void postOk() throws Exception {
        var longDescription = "d".repeat(200);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"description\": \"" + longDescription + "\"," +
                        "\"releaseDate\": \"1895-12-28\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"F\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void putValidationFailName() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"duration\": 1}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": null,\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-29\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-29\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void putValidationFailDescription() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"duration\": 1}");

        mockMvc.perform(mockRequest);

        var longDescription = "d".repeat(201);
        mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"Film name\",\"description\": \"" + longDescription + "\"," +
                        "\"releaseDate\": \"1895-12-29\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void putValidationFailReleaseDate() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"duration\": 1}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"Film name\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-27\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void putValidationFailDuration() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Film name\",\"duration\": 1}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"Film name\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-28\",\"duration\": 0}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));

        mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"Film name\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-28\",\"duration\": -1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation exception")));
    }

    @Test
    void putOk() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"F\",\"duration\": 1}");

        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"Film name\",\"description\": \"Film description\"," +
                        "\"releaseDate\": \"1895-12-28\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"n\",\"duration\": 1}");

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }
}