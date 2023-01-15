package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.ProjectDto;
import net.noahk.takenlijst.security.JwtService;
import net.noahk.takenlijst.services.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;

@WebMvcTest(ProjectsController.class)
class ProjectsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtService jwtService;

    @MockBean
    ProjectService service;

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void getProject() throws Exception {
        var dto = new ProjectDto();

        var members = new ArrayList<String>();
        members.add("Noah");
        members.add("Test");

        dto.name = "Project";
        dto.members = members;

        Mockito.when(service.getProject(anyLong())).thenReturn(Optional.of(dto));
        Mockito.when(service.isProjectMember(ArgumentMatchers.any(), anyLong())).thenReturn(true);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/projects/1/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Project")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.members", hasItems("Noah", "Test")));
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void cannotViewWhenNotInProject() throws Exception {
        var dto = new ProjectDto();

        var members = new ArrayList<String>();
        members.add("Noah");
        members.add("Test");

        dto.name = "Project";
        dto.members = members;

        Mockito.when(service.getProject(anyLong())).thenReturn(Optional.of(dto));
        Mockito.when(service.isProjectMember(ArgumentMatchers.any(), anyLong())).thenReturn(false);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/projects/1/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}