package com.drbaltar.continuityweek4.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Crewmember {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int morale;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMorale() {
        return morale;
    }

    public void setMorale(int morale) {
        this.morale = morale;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
