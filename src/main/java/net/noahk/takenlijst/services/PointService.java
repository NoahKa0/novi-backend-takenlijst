package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.PointDto;
import net.noahk.takenlijst.models.Point;
import net.noahk.takenlijst.models.Task;
import net.noahk.takenlijst.repositories.PointRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class PointService {

    private final PointRepository repository;

    public PointService(PointRepository repository) {this.repository = repository;}

    public Iterable<PointDto> getPoints() {
        var items = repository.findAll();
        var list = new ArrayList<PointDto>();

        for (var item : items) {
            var dto = new PointDto();

            dto = fillDto(item, dto);

            list.add(dto);
        }
        return list;
    }

    public Optional<PointDto> getPoint(Long id) {
        var record = repository.findById(id);
        if (record.isEmpty()) {
            return Optional.empty();
        }
        var item = record.get();

        var dto = new PointDto();

        dto = fillDto(item, dto);

        return Optional.of(dto);
    }

    public boolean update(Long id, PointDto point) {
        var item = repository.findById(id);
        if (item.isPresent()) {
            var itemToUpdate = item.get();

            itemToUpdate = fillEntity(itemToUpdate, point);

            repository.save(itemToUpdate);
            return true;
        }
        return false;
    }

    public Long create(PointDto point) {
        var toSave = new Point();

        toSave = fillEntity(toSave, point);

        var result = repository.save(toSave);
        return result.getId();
    }

    protected static Point fillEntity(Point entity, PointDto dto) {
        entity.setDescription(dto.description);
        entity.setActualPoints(dto.actualPoints);
        entity.setExpectedPoints(dto.expectedPoints);
        if (dto.taskId != 0) {
            var task = new Task();
            task.setId(dto.taskId);
            entity.setTask(task);
        }

        return entity;
    }

    protected static PointDto fillDto(Point entity, PointDto dto) {
        dto.id = entity.getId();
        dto.description = entity.getDescription();
        dto.actualPoints = entity.getActualPoints();
        dto.expectedPoints = entity.getExpectedPoints();

        return dto;
    }
}
