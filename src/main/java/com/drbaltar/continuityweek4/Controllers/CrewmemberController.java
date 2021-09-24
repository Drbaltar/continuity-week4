package com.drbaltar.continuityweek4.Controllers;

import com.drbaltar.continuityweek4.Models.Crewmember;
import com.drbaltar.continuityweek4.Repositories.CrewmemberRepository;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{id}")
    public String deleteCrewmemberById(@PathVariable Long id) {
        repository.deleteById(id);
        return "The crewmember with an id of %d has been deleted from the database".formatted(id);
    }
}
