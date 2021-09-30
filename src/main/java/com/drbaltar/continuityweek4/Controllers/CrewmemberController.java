package com.drbaltar.continuityweek4.Controllers;

import com.drbaltar.continuityweek4.Models.Crewmember;
import com.drbaltar.continuityweek4.Repositories.CrewmemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/crewmember")
public class CrewmemberController {

    CrewmemberRepository repository;

    public CrewmemberController(CrewmemberRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Crewmember saveCrewmemberEntryInDB(@RequestBody Crewmember newCrewmember) {
        return repository.save(newCrewmember);
    }

    @GetMapping("/{id}")
    public Optional<Crewmember> getCrewmemberByID(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping
    public Iterable<Crewmember> getAllCrewmembers() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    public Crewmember updateCrewmemberByID(@PathVariable Long id, @RequestBody Crewmember updatedCrewmember) {
        updatedCrewmember.setId(id);
        return repository.save(updatedCrewmember);
    }

    @PatchMapping("/{id}")
    public Optional<Crewmember> updateCrewmemberFieldsByID(@PathVariable Long id, @RequestBody HashMap<String, String> updatedFields) {
        var queryResults = repository.findById(id);
        return queryResults.map(crewmember -> {
            return updateFields(crewmember, updatedFields);
        });
    }

    private Crewmember updateFields(Crewmember crewmember, HashMap<String, String> updatedFields) {
        updatedFields.forEach((key, value) -> {
            if (key.equals("name"))
                crewmember.setName(value);
            else if (key.equals("morale"))
                crewmember.setMorale(Integer.parseInt(value));
        });
        return crewmember;
    }

    @DeleteMapping("/{id}")
    public String deleteCrewmemberById(@PathVariable Long id) {
        repository.deleteById(id);
        return "The crewmember with an id of %d has been deleted from the database".formatted(id);
    }
}
