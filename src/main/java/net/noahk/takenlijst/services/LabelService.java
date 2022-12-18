package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.LabelDto;
import net.noahk.takenlijst.models.Label;
import net.noahk.takenlijst.repositories.LabelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class LabelService {

    private final LabelRepository repository;

    public LabelService(LabelRepository repository) {this.repository = repository;}

    public Iterable<LabelDto> getLabels() {
        var items = repository.findAll();
        var list = new ArrayList<LabelDto>();

        for (var item : items) {
            var dto = new LabelDto();

            dto = fillDto(item, dto);

            list.add(dto);
        }
        return list;
    }

    public Optional<LabelDto> getLabel(Long id) {
        var record = repository.findById(id);
        if (record.isEmpty()) {
            return Optional.empty();
        }
        var item = record.get();

        var dto = new LabelDto();

        dto = fillDto(item, dto);

        return Optional.of(dto);
    }

    public boolean update(Long id, LabelDto label) {
        var item = repository.findById(id);
        if (item.isPresent()) {
            var itemToUpdate = item.get();

            itemToUpdate = fillEntity(itemToUpdate, label);

            repository.save(itemToUpdate);
            return true;
        }
        return false;
    }

    public Long create(LabelDto label) {
        var toSave = new Label();

        toSave = fillEntity(toSave, label);

        var result = repository.save(toSave);
        return result.getId();
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    protected static Label fillEntity(Label entity, LabelDto dto) {
        entity.setName(dto.name);
        entity.setRed(dto.red);
        entity.setGreen(dto.green);
        entity.setBlue(dto.blue);

        return entity;
    }

    protected static LabelDto fillDto(Label entity, LabelDto dto) {
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.red = entity.getRed();
        dto.green = entity.getGreen();
        dto.blue = entity.getBlue();

        return dto;
    }
}
