package com.ironhack.exceptionstesting.service;

import com.ironhack.exceptionstesting.exception.ProjectNotFoundException;
import com.ironhack.exceptionstesting.model.Project;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final List<Project> projects = new ArrayList<>();
    private Long nextId = 1L;

    public List<Project> findAll() {
        return projects;
    }

    public Project findById(Long id) {
        return projects.stream()
                .filter(project -> project.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));
    }

    public Project addProject(Project project) {
        project.setId(nextId++);
        projects.add(project);
        return project;
    }

    public Project updateProject(Long id, Project updatedProject) {
        Project existingProject = projects.stream()
                .filter(project -> project.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));

        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setTime(updatedProject.getTime());

        return existingProject;
    }

    public void deleteProject(Long id) {
        Project project = projects.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));

        projects.remove(project);
    }
}

