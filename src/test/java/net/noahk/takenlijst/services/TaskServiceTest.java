package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.models.*;
import net.noahk.takenlijst.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository repository;

    @InjectMocks
    TaskService service;

    @Test
    void getBurnDown() {
        // Arrange
        var list = new ArrayList<Task>();

        list.add(createTask(5, LocalDate.of(2023, 1, 1),"Task"));

        list.add(createTask(6, LocalDate.of(2023, 1, 2),"Task"));
        list.add(createTask(6, LocalDate.of(2023, 1, 2),"Task"));

        list.add(createTask(4, LocalDate.of(2023, 1, 4),"Task"));

        Mockito.when(repository.getTasksByProjectIdAndCompletedAtBetween(anyLong(), any(LocalDate.class), any(LocalDate.class))).thenReturn(list);

        // Act
        var start = LocalDate.of(2023, 1, 1);
        var end = LocalDate.of(2023, 1, 5);
        var result = service.getBurnDown(1, start, end, false);

        // Assert
        assertEquals(21, result.get(0)); // 5 + 6 + 6 + 4 = 21
        assertEquals(16, result.get(1)); // 6 + 6 + 4 = 16
        assertEquals(4, result.get(2));
        assertEquals(4, result.get(3));
        assertEquals(0, result.get(4));
    }

    @Test
    void getBurnDownPredicted() {
        // Arrange
        var list = new ArrayList<Task>();

        list.add(createTask(5, LocalDate.of(2023, 1, 1),"Task"));

        list.add(createTask(6, LocalDate.of(2023, 1, 2),"Task"));
        list.add(createTask(6, LocalDate.of(2023, 1, 2),"Task"));

        list.add(createTask(4, LocalDate.of(2023, 1, 4),"Task"));

        Mockito.when(repository.getTasksByProjectIdAndCompletedAtBetween(anyLong(), any(LocalDate.class), any(LocalDate.class))).thenReturn(list);

        // Act
        var start = LocalDate.of(2023, 1, 1);
        var end = LocalDate.of(2023, 1, 5);
        var result = service.getBurnDown(1, start, end, true);

        // Assert
        assertEquals(21, result.get(0)); // 5 + 6 + 6 + 4 = 21
        assertEquals(16, result.get(1)); // 6 + 6 + 4 = 16
        assertEquals(4, result.get(2));
        assertEquals(4, result.get(3));
        assertEquals(0, result.get(4));
    }

    @Test
    void getTasks() {
        // Arrange
        var a = new Task();
        a.setName("Test");
        a.setDescription("Description");
        a.setId(1l);

        var b = new Task();
        b.setName("Test");
        b.setDescription("Description");
        b.setId(2l);

        var items = new ArrayList<Task>();
        items.add(a);
        items.add(b);
        Mockito.when(repository.findAll()).thenReturn(items);

        // Act
        var result = service.getTasks();

        // Assert
        assertTrue(result.stream().anyMatch(x -> x.id == 1l));
        assertTrue(result.stream().anyMatch(x -> x.id == 2l));
        assertFalse(result.stream().anyMatch(x -> x.id == 3l));
    }

    @Test
    void getTasksByProject() {
        // Arrange
        var label = new Label();
        label.setId(5l);

        var a = new Task();
        a.setName("Test");
        a.setDescription("Description");
        a.setId(1l);
        a.setLabel(label);

        var b = new Task();
        b.setName("Test");
        b.setDescription("Description");
        b.setCompletedAt(LocalDate.of(2023, 1, 4));
        b.setId(2l);

        var items = new ArrayList<Task>();
        items.add(a);
        items.add(b);
        Mockito.when(repository.getTasksByProjectId(anyLong())).thenReturn(items);

        // Act
        var result = service.getTasksByProject(1l, false);
        var resultNonCompleted = service.getTasksByProject(1l, true);

        // Assert
        assertTrue(result.stream().anyMatch(x -> x.id == 1l));
        assertTrue(result.stream().anyMatch(x -> x.id == 2l));
        assertFalse(result.stream().anyMatch(x -> x.id == 3l));

        assertTrue(resultNonCompleted.stream().anyMatch(x -> x.id == 1l));
        assertFalse(resultNonCompleted.stream().anyMatch(x -> x.id == 2l));
        assertFalse(resultNonCompleted.stream().anyMatch(x -> x.id == 3l));
    }

    @Test
    void getPoint() {
        // Arrange
        var a = new Task();
        a.setName("Test");
        a.setDescription("Description");
        a.setId(1l);

        var user = new User();
        user.setUsername("Test");

        var project = new Project();
        project.setId(2l);

        var label = new Label();
        label.setId(3l);

        var c = new Comment();
        c.setId(11l);
        c.setUser(user);
        var comments = new ArrayList<Comment>();
        comments.add(c);

        var p = new Point();
        p.setId(12l);
        var points = new ArrayList<Point>();
        points.add(p);

        a.setUser(user);
        a.setProject(project);
        a.setLabel(label);
        a.setComments(comments);
        a.setPoints(points);

        Mockito.when(repository.findById(1l)).thenReturn(Optional.of(a));
        Mockito.when(repository.findById(2l)).thenReturn(Optional.empty());

        // Act
        var resultA = service.getTask(1l);
        var resultB = service.getTask(2l);

        // Assert
        assertTrue(resultA.isPresent());
        assertTrue(resultB.isEmpty());

        assertEquals(1l, resultA.get().id);
        assertEquals("Test", resultA.get().name);
        assertEquals("Description", resultA.get().description);
    }

    @Test
    void update() {
        // Arrange
        var target = new Task();
        target.setName("Test");
        target.setDescription("Description");
        target.setId(1l);

        var update = new TaskDto();
        update.name = "Test";
        update.description = "Test";
        update.labelId = 5;
        update.projectId = 6;
        update.assignedUser = "user";

        Mockito.when(repository.findById(1l)).thenReturn(Optional.of(target));
        Mockito.when(repository.findById(2l)).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(Task.class))).thenReturn(target);

        // Act
        var updateExisting = service.update(1l, update);
        var updateNonExisting = service.update(2l, update);

        // Assert
        assertTrue(updateExisting);
        assertFalse(updateNonExisting);
    }

    @Test
    void create() {
        // Arrange
        var saved = new Task();
        saved.setName("Test");
        saved.setDescription("Description");
        saved.setId(5l);

        var create = new TaskDto();
        create.description = "Desc";

        Mockito.when(repository.save(any(Task.class))).thenReturn(saved);

        // Act
        var savedId = service.create(create);

        // Assert
        assertEquals(5l, savedId);
    }

    @Test
    void delete() {
        // Act
        service.delete(1l);

        // Assert
        verify(repository, times(1)).deleteById(1l);
    }

    private Task createTask(int amountOfPoints, LocalDate completedAt, String name) {
        var ret = new Task();
        ret.setName(name);
        ret.setCompletedAt(completedAt);

        var point = new Point();
        point.setExpectedPoints(amountOfPoints);
        point.setActualPoints(amountOfPoints);

        var points = new ArrayList<Point>();
        points.add(point);

        ret.setPoints(points);

        return ret;
    }
}