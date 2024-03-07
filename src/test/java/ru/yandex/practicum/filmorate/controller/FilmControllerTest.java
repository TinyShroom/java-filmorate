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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ValidationException;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FilmService filmService;

    private static ObjectMapper mapper;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

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
        var content = "{\"name\": \"Film name\",\"description\": \"Film description\"," +
                "\"releaseDate\": \"1895-12-27\",\"duration\": 1}";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        when(filmService.addFilm(mapper.readValue(content, Film.class)))
                .thenThrow(new ValidationException("POST /films: release date must be after 1895-12-28"));
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("POST /films: release date must be after 1895-12-28")));
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
        var content = "{\"name\": \"Film name\",\"description\": \"" + longDescription + "\"," +
                "\"releaseDate\": \"1895-12-28\",\"duration\": 1}";
        var answer = "{\"id\": 1,\"name\": \"Film name\",\"description\": \"" + longDescription + "\"," +
                "\"releaseDate\": \"1895-12-28\",\"duration\": 1}";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        when(filmService.addFilm(mapper.readValue(content, Film.class)))
                .thenReturn(mapper.readValue(answer, Film.class));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        content = "{\"name\": \"F\",\"duration\": 1}";
        answer = "{\"id\": 1,\"name\": \"F\",\"duration\": 1}";

        mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"F\",\"duration\": 1}");

        when(filmService.addFilm(mapper.readValue(content, Film.class)))
                .thenReturn(mapper.readValue(answer, Film.class));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
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
        var content = "{\"id\": 1,\"name\": \"Film name\",\"description\": \"Film description\"," +
                "\"releaseDate\": \"1895-12-27\",\"duration\": 1}";

        var mockRequest = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        when(filmService.addFilm(mapper.readValue(content, Film.class)))
                .thenThrow(new ValidationException("PUT /films: release date must be after 1895-12-28"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("PUT /films: release date must be after 1895-12-28")));
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
        var content = "{\"id\": 1,\"name\": \"Film name\",\"description\": \"Film description\"," +
                "\"releaseDate\": \"1895-12-28\",\"duration\": 1}";

        var mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        var film = mapper.readValue(content, Film.class);
        when(filmService.changeFilm(film)).thenReturn(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        content = "{\"id\": 1,\"name\": \"n\",\"duration\": 1}";

        mockRequest = MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        film = mapper.readValue(content, Film.class);

        when(filmService.changeFilm(film)).thenReturn(film);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }
}