package com.drbaltar.continuityweek4.Controllers;

import com.drbaltar.continuityweek4.Models.Crewmember;
import com.drbaltar.continuityweek4.Models.Spaceship;
import com.drbaltar.continuityweek4.Repositories.CrewmemberRepository;
import com.drbaltar.continuityweek4.Repositories.SpaceshipRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/spaceship")
public class SpaceshipController {

    private final SpaceshipRepository repository;

    public SpaceshipController(SpaceshipRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Spaceship saveSpaceshipToDB(@RequestBody Spaceship newSpaceship) {
        return repository.save(newSpaceship);
    }

    @GetMapping("/{id}")
    public Optional<Spaceship> getIndividualSpaceshipById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping
    public Iterable<Spaceship> getAllSpaceshipsInDB() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    public Spaceship updateSpaceshipByID(@PathVariable Long id, @RequestBody Spaceship updatedSpaceship) {
        updatedSpaceship.setId(id);
        return repository.save(updatedSpaceship);
    }

    @PatchMapping("/{id}")
    public Optional<Spaceship> updateSpaceshipFieldsByID(@PathVariable Long id, @RequestBody HashMap<String, String> updatedFields) {
        var queryResults = repository.findById(id);
        return queryResults.map(spaceship -> {
            return updateFields(spaceship, updatedFields);
        });
    }

    private Spaceship updateFields(Spaceship spaceship, HashMap<String, String> updatedFields) {
        updatedFields.forEach((key, value) -> {
            if (key.equals("name"))
                spaceship.setName(value);
            else if (key.equals("fuel"))
                spaceship.setFuel(Integer.parseInt(value));
        });
        return spaceship;
    }

    @DeleteMapping("/{id}")
    public String deleteSpaceshipById(@PathVariable Long id) {
        repository.deleteById(id);
        return "The spaceship with an id of %d has been deleted from the database".formatted(id);
    }

    @GetMapping("/current")
    public String getCurrentSpaceshipNumber(@CookieValue(value = "current", required = false) String id) {
        if (id == null)
            return "You do not have a current spaceship";
        else
            return "Your current spaceship has the id of %s".formatted(id);
    }
}
