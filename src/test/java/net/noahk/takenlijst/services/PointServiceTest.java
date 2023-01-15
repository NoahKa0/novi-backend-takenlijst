package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.PointDto;
import net.noahk.takenlijst.models.Point;
import net.noahk.takenlijst.models.Task;
import net.noahk.takenlijst.repositories.PointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    PointRepository repository;

    @InjectMocks
    PointService service;

    @Test
    void getPoints() {
        // Arrange
        var a = new Point();
        a.setExpectedPoints(5);
        a.setActualPoints(7);
        a.setId(1l);

        var b = new Point();
        b.setExpectedPoints(5);
        b.setActualPoints(7);
        b.setId(2l);

        var items = new ArrayList<Point>();
        items.add(a);
        items.add(b);
        Mockito.when(repository.findAll()).thenReturn(items);

        // Act
        var result = service.getPoints();

        // Assert
        assertTrue(result.stream().anyMatch(x -> x.id == 1l));
        assertTrue(result.stream().anyMatch(x -> x.id == 2l));
        assertFalse(result.stream().anyMatch(x -> x.id == 3l));
    }

    @Test
    void getPoint() {
        // Arrange
        var a = new Point();
        a.setDescription("Test");
        a.setExpectedPoints(5);
        a.setActualPoints(7);
        a.setId(1l);

        var task = new Task();
        task.setId(5l);
        a.setTask(task);

        Mockito.when(repository.findById(1l)).thenReturn(Optional.of(a));
        Mockito.when(repository.findById(2l)).thenReturn(Optional.empty());

        // Act
        var resultA = service.getPoint(1l);
        var resultB = service.getPoint(2l);

        // Assert
        assertTrue(resultA.isPresent());
        assertTrue(resultB.isEmpty());

        assertEquals(1l, resultA.get().id);
        assertEquals("Test", resultA.get().description);
        assertEquals(7, resultA.get().actualPoints);
        assertEquals(5, resultA.get().expectedPoints);
    }

    @Test
    void update() {
        // Arrange
        var target = new Point();
        target.setDescription("Test");
        target.setExpectedPoints(5);
        target.setActualPoints(7);
        target.setId(1l);

        var update = new PointDto();
        update.description = "Test";

        Mockito.when(repository.findById(1l)).thenReturn(Optional.of(target));
        Mockito.when(repository.findById(2l)).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(Point.class))).thenReturn(target);

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
        var saved = new Point();
        saved.setDescription("Test");
        saved.setExpectedPoints(5);
        saved.setActualPoints(7);
        saved.setId(5l);

        var create = new PointDto();
        create.description = "Desc";
        create.taskId = 5;

        Mockito.when(repository.save(any(Point.class))).thenReturn(saved);

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
}