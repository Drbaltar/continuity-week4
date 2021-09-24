package com.drbaltar.continuityweek4.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Spaceship {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int fuel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
