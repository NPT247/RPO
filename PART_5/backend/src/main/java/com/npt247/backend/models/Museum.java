package com.npt247.backend.models;

import javax.persistence.*;

@Entity
@Table(name = "museums")
@Access(AccessType.FIELD)
public class Museum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name= "location")
    private String location;

    public Museum(){}
    public Museum(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
