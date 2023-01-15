package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.LabelDto;
import net.noahk.takenlijst.models.Label;
import net.noahk.takenlijst.repositories.LabelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock
    LabelRepository repository;

    @InjectMocks
    LabelService service;

    @Test
    void getLabels() {
        // Arrange
        var a = new Label();
        a.setName("A");
        a.setId(1l);

        var b = new Label();
        b.setName("B");
        b.setId(2l);

        var items = new ArrayList<Label>();
        items.add(a);
        items.add(b);
        Mockito.when(repository.findAll()).thenReturn(items);

        // Act
        var result = service.getLabels();

        // Assert
        assertTrue(result.stream().anyMatch(x -> x.id == 1l));
        assertTrue(result.stream().anyMatch(x -> x.id == 2l));
        assertFalse(result.stream().anyMatch(x -> x.id == 3l));
    }

    @Test
    void getLabel() {
        // Arrange
        var a = new Label();
        a.setName("ABC");
        a.setId(1l);

        Mockito.when(repository.findById(1l)).thenReturn(Optional.of(a));
        Mockito.when(repository.findById(2l)).thenReturn(Optional.empty());

        // Act
        var resultA = service.getLabel(1l);
        var resultB = service.getLabel(2l);

        // Assert
        assertTrue(resultA.isPresent());
        assertTrue(resultB.isEmpty());

        assertEquals(1l, resultA.get().id);
        assertEquals("ABC", resultA.get().name);
        assertEquals(0, resultA.get().red);
        assertEquals(0, resultA.get().green);
        assertEquals(0, resultA.get().blue);
    }

    @Test
    void update() {
        // Arrange
        var target = new Label();
        target.setName("ABC");
        target.setId(1l);

        var update = new LabelDto();
        update.name = "Test";

        Mockito.when(repository.findById(1l)).thenReturn(Optional.of(target));
        Mockito.when(repository.findById(2l)).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(Label.class))).thenReturn(target);

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
        var saved = new Label();
        saved.setName("ABC");
        saved.setId(5l);

        var create = new LabelDto();
        create.name = "Test";

        Mockito.when(repository.save(any(Label.class))).thenReturn(saved);

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