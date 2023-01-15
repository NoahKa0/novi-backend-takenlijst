package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.security.JwtService;
import net.noahk.takenlijst.services.ProjectService;
import net.noahk.takenlijst.services.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

import static org.hamcrest.Matchers.is;

@WebMvcTest(TasksController.class)
class TasksControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtService jwtService;

    @MockBean
    TaskService service;

    @MockBean
    ProjectService projectService;

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void getTask() throws Exception {
        var dto = new TaskDto();

        dto.name = "Task";
        dto.description = "Abc";
        dto.projectId = 5;

        Mockito.when(service.getTask(anyLong())).thenReturn(Optional.of(dto));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/tasks/1/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Task")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", is("Abc")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.projectId", is(5)));
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void getTask404() throws Exception {
        var dto = new TaskDto();

        dto.name = "Task";
        dto.description = "Abc";
        dto.projectId = 5;

        Mockito.when(service.getTask(anyLong())).thenReturn(Optional.empty());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/tasks/1/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}