package com.ironhack.exceptionstesting;

import com.ironhack.exceptionstesting.exception.ProjectNotFoundException;
import com.ironhack.exceptionstesting.model.Project;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ironhack.exceptionstesting.service.ProjectService;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    private Project createTestProject(Long id, String name, String description) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setTime(LocalDateTime.of(2026, 2, 7, 10, 0, 0));
        return project;
    }

    @Test
    void getAllProjects_returnsOk() throws Exception {
        Project p1 = createTestProject(1L, "IronBoard", "Project management app");
        Project p2 = createTestProject(2L, "IronLibrary", "Library management system");

        when(projectService.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/projects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("IronBoard"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("IronLibrary"));
    }

    @Test
    void getProjectById_existingId_returnsOk() throws Exception {
        Project project = createTestProject(1L, "IronBoard", "Project management app");

        when(projectService.findById(1L)).thenReturn(project);

        mockMvc.perform(get("/projects/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("IronBoard"))
                .andExpect(jsonPath("$.description").value("Project management app"));
    }

    @Test
    void getProjectById_nonExistingId_returns404() throws Exception {
        when(projectService.findById(99L))
                .thenThrow(new ProjectNotFoundException("Project with id 99 not found"));

        mockMvc.perform(get("/projects/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Project with id 99 not found"));
    }

    @Test
    void createProject_validBody_returnsOk() throws Exception {
        Project requestProject = createTestProject(null, "IronBoard", "Project management app");
        Project savedProject = createTestProject(1L, "IronBoard", "Project management app");

        when(projectService.addProject(any(Project.class))).thenReturn(savedProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestProject)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("IronBoard"))
                .andExpect(jsonPath("$.description").value("Project management app"));
    }

    @Test
    void createProject_invalidBody_returns400() throws Exception {
        Project invalidProject = new Project();
        invalidProject.setName("");
        invalidProject.setDescription("Invalid project");
        invalidProject.setTime(null);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProject)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void updateProject_existingId_returnsOk() throws Exception {
        Project requestProject = createTestProject(null, "Updated Project", "Updated description");
        Project updatedProject = createTestProject(1L, "Updated Project", "Updated description");

        when(projectService.updateProject(eq(1L), any(Project.class))).thenReturn(updatedProject);

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestProject)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Project"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateProject_nonExistingId_returns404() throws Exception {
        Project requestProject = createTestProject(null, "Updated Project", "Updated description");

        when(projectService.updateProject(eq(99L), any(Project.class)))
                .thenThrow(new ProjectNotFoundException("Project with id 99 not found"));

        mockMvc.perform(put("/projects/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestProject)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Project with id 99 not found"));
    }

    @Test
    void updateProject_invalidBody_returns400() throws Exception {
        Project invalidProject = new Project();
        invalidProject.setName("");
        invalidProject.setDescription("Invalid update");
        invalidProject.setTime(null);

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProject)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void deleteProject_existingId_returnsOk() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/projects/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Project deleted successfully"));
    }

    @Test
    void deleteProject_nonExistingId_returns404() throws Exception {
        doThrow(new ProjectNotFoundException("Project with id 99 not found"))
                .when(projectService).deleteProject(99L);

        mockMvc.perform(delete("/projects/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Project with id 99 not found"));
    }
}
