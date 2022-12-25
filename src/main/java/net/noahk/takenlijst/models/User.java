package net.noahk.takenlijst.models;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="users")
public class User {
    @Id
    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles;

    @ManyToMany
    private Collection<Project> projects;

    @OneToMany(mappedBy = "user")
    private List<Task> tasks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
