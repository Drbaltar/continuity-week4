package com.drbaltar.continuityweek4.Controllers;

import com.drbaltar.continuityweek4.Models.Spaceship;
import com.drbaltar.continuityweek4.Repositories.SpaceshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SpaceshipControllerTest {

    private final String[] testSpaceshipNames = {"Apollo 11", "Battlestar Galactica", "Apollo 13"};

    @Autowired
    MockMvc mvc;
    @Autowired
    SpaceshipRepository repository;

    @Test
    @Transactional
    @Rollback
    void shouldSaveSpaceshipToDB() throws Exception {
        String testSpaceshipJSON = """
                {
                  "name": "Apollo 11",
                  "fuel": 100
                }
                """;
        var request = post("/spaceship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testSpaceshipJSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", is("Apollo 11")))
                .andExpect(jsonPath("$.fuel", is(100)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetIndividualSpaceshipById() throws Exception {
        var testSpaceship = getTestSpaceship("Apollo 13");
        testSpaceship = repository.save(testSpaceship);

        var request = get("/spaceship/%d".formatted(testSpaceship.getId()));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(testSpaceship.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Apollo 13")))
                .andExpect(jsonPath("$.fuel", is(100)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetAllSpaceshipsInDB() throws Exception {
        populateDBWithTestSpaceships();

        var request = get("/spaceship");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(testSpaceshipNames.length)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyIfNoSpaceshipsInDB() throws Exception {
        var request = get("/spaceship");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteSpaceshipById() throws Exception {
        var testSpaceship = populateDBWithTestSpaceships()[0];

        var request = delete("/spaceship/%d".formatted(testSpaceship.getId()));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("The spaceship with an id of %d has been deleted from the database".formatted(testSpaceship.getId())));
        assertEquals(testSpaceshipNames.length - 1, getSizeOfDatabase());
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnCurrentSpaceshipFromCookieValueWhenPresent() throws Exception {
        var testSpaceship = populateDBWithTestSpaceships()[0];

        var request = get("/spaceship/current")
                .cookie(new Cookie("current", String.valueOf(testSpaceship.getId())));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("Your current spaceship has the id of %d".formatted(testSpaceship.getId())));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnMessageWithNoIDIfCookieValueNotPresent() throws Exception {
        var request = get("/spaceship/current");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("You do not have a current spaceship"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateEntireSpaceshipEntryByID() throws Exception {
        var testSpaceship = populateDBWithTestSpaceships()[0];
        String testSpaceshipJSON = """
                {
                  "name": "Challenger",
                  "fuel": 90
                }
                """;
        var request = put("/spaceship/%d".formatted(testSpaceship.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testSpaceshipJSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testSpaceship.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Challenger")))
                .andExpect(jsonPath("$.fuel", is(90)));

        assertEquals(testSpaceshipNames.length, getSizeOfDatabase());
    }

    @ParameterizedTest
    @CsvSource(value = {"Challenger, 100", "Apollo 11, 95"})
    @Transactional
    @Rollback
    void shouldUpdateSpaceshipPropertiesByID(String name, int fuel) throws Exception {
        var testSpaceship = populateDBWithTestSpaceships()[0];
        String testSpaceshipJSON;
        if (testSpaceship.getName().equals(name))
            testSpaceshipJSON = "{ \"fuel\": %d }".formatted(fuel);
        else
            testSpaceshipJSON = "{ \"name\": \"%s\" }".formatted(name);

        var request = patch("/spaceship/%d".formatted(testSpaceship.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testSpaceshipJSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testSpaceship.getId().intValue())))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.fuel", is(fuel)));

//        assertEquals(testSpaceshipNames.length, getSizeOfDatabase());
    }

    private int getSizeOfDatabase() {
        var dbEntries = repository.findAll();
        AtomicInteger sizeOfDB = new AtomicInteger();
        dbEntries.forEach(entry -> sizeOfDB.getAndIncrement());
        return sizeOfDB.get();
    }

    private Spaceship[] populateDBWithTestSpaceships() {
        var testSpaceshipArray = getTestSpaceshipArray(testSpaceshipNames);
        repository.saveAll(Arrays.asList(testSpaceshipArray));
        return testSpaceshipArray;
    }

    private Spaceship[] getTestSpaceshipArray(String[] names) {
        var testSpaceshipArray = new Spaceship[names.length];
        for (int i = 0; i < names.length; i++)
            testSpaceshipArray[i] = getTestSpaceship(names[i]);
        return testSpaceshipArray;
    }

    private Spaceship getTestSpaceship(String name) {
        var testSpaceship = new Spaceship();
        testSpaceship.setName(name);
        testSpaceship.setFuel(100);
        return testSpaceship;
    }
}
