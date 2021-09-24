package com.drbaltar.continuityweek4.Controllers;

import com.drbaltar.continuityweek4.Models.Crewmember;
import com.drbaltar.continuityweek4.Repositories.CrewmemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CrewmemberControllerTest {

    private final String[] testCrewmemberNames = {"Jennifer", "Joe", "Bob"};

    @Autowired
    MockMvc mvc;
    @Autowired
    CrewmemberRepository repository;

    @Test
    @Transactional
    @Rollback
    void shouldCreateNewCrewmemberInTheDB() throws Exception {
        String testCrewmemberJSON = """
                {
                    "name": "Alice",
                    "morale": 100
                }
                """;
        var request = post("/crewmember")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testCrewmemberJSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.morale", is(100)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetIndividualCrewmemberById() throws Exception {
        Crewmember testCrewmember = getTestCrewmember("Jennifer");
        testCrewmember = repository.save(testCrewmember);

        var request = get("/crewmember/%d".formatted(testCrewmember.getId()));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(testCrewmember.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testCrewmember.getName())))
                .andExpect(jsonPath("$.morale", is(testCrewmember.getMorale())));
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetAllCrewmembers() throws Exception {
        populateDBWithTestCrewmembers();

        var request = get("/crewmember");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(testCrewmemberNames.length)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyListIfNoCrewMembersInDB() throws Exception {
        var request = get("/crewmember");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteCrewmemberById() throws Exception {
        var testCrewmember = populateDBWithTestCrewmembers()[0];

        var request = delete("/crewmember/%d".formatted(testCrewmember.getId()));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("The crewmember with an id of %d has been deleted from the database".formatted(testCrewmember.getId())));

        assertEquals(testCrewmemberNames.length - 1, getSizeOfDatabase());
    }

    private int getSizeOfDatabase() {
        var dbEntries = repository.findAll();
        AtomicInteger sizeOfDB = new AtomicInteger();
        dbEntries.forEach(entry -> sizeOfDB.getAndIncrement());
        return sizeOfDB.get();
    }

    private Crewmember[] populateDBWithTestCrewmembers() {
        var crewMemberArray = getTestCrewmemberArray(testCrewmemberNames);
        repository.saveAll(Arrays.asList(crewMemberArray));
        return crewMemberArray;
    }

    private Crewmember[] getTestCrewmemberArray(String[] names) {
        var crewMemberArray = new Crewmember[names.length];
        for (int i = 0; i < names.length; i++)
            crewMemberArray[i] = getTestCrewmember(names[i]);
        return crewMemberArray;
    }

    private Crewmember getTestCrewmember(String name) {
        var testCrewmember = new Crewmember();
        testCrewmember.setName(name);
        testCrewmember.setMorale(100);
        return testCrewmember;
    }
}
