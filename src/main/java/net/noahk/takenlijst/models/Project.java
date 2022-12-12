package net.noahk.takenlijst.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="projects")
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Long projectLeaderId;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

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

    public Long getProjectLeaderId() {
        return projectLeaderId;
    }

    public void setProjectLeaderId(Long projectLeaderId) {
        this.projectLeaderId = projectLeaderId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
