package com.ironhack.exceptionstesting.model;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class Project {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 200, message = "Name must be 2-200 characters")
    private String name;
    private String description;
    private LocalDateTime time;

    public Project(Long id, String name, String description, LocalDateTime time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
    }

    public Project() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

}

