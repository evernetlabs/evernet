package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.evernet.embedded.Event;
import xyz.evernet.embedded.Function;
import xyz.evernet.embedded.Property;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Structure;
import xyz.evernet.repository.StructureRepository;
import xyz.evernet.request.StructureCreationRequest;
import xyz.evernet.util.JsonSchemaUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StructureService {

    private final StructureRepository structureRepository;

    public Structure create(StructureCreationRequest request, String creator) {

        if (structureRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException("Structure %s already exists".formatted(request.getIdentifier()));
        }

        if (!CollectionUtils.isEmpty(request.getProperties())) {
            for (Map.Entry<String, Property> entry : request.getProperties().entrySet()) {
                Property property = entry.getValue();

                if (StringUtils.hasText(property.getSchema())) {
                    if (!JsonSchemaUtil.isValidSchema(property.getSchema())) {
                        throw new ClientException("Invalid JSON schema for property %s".formatted(entry.getKey()));
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(request.getFunctions())) {
            for (Map.Entry<String, Function> entry : request.getFunctions().entrySet()) {
                Function function = entry.getValue();

                if (StringUtils.hasText(function.getInputSchema())) {
                    if (!JsonSchemaUtil.isValidSchema(function.getInputSchema())) {
                        throw new ClientException("Invalid JSON schema for function %s input".formatted(entry.getKey()));
                    }
                }
                if (StringUtils.hasText(function.getOutputSchema())) {
                    if (!JsonSchemaUtil.isValidSchema(function.getOutputSchema())) {
                        throw new ClientException("Invalid JSON schema for function %s output".formatted(entry.getKey()));
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(request.getEvents())) {
            for (Map.Entry<String, Event> entry : request.getEvents().entrySet()) {
                Event event = entry.getValue();

                if (StringUtils.hasText(event.getSchema())) {
                    if (!JsonSchemaUtil.isValidSchema(event.getSchema())) {
                        throw new ClientException("Invalid JSON schema for event %s".formatted(entry.getKey()));
                    }
                }
            }
        }

        Set<String> managementRoles = request.getManagementRoles();

        if (CollectionUtils.isEmpty(managementRoles)) {
            managementRoles = new HashSet<>();
        }

        managementRoles.add("creator");

        Structure structure = Structure.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .properties(request.getProperties())
                .functions(request.getFunctions())
                .events(request.getEvents())
                .managementRoles(managementRoles)
                .creator(creator)
                .build();

        return structureRepository.save(structure);
    }

    public List<Structure> list(Pageable pageable) {
        return structureRepository.findAll(pageable).getContent();
    }

    public Structure get(String identifier) {
        Structure structure = structureRepository.findByIdentifier(identifier);

        if (structure == null) {
            throw new NotFoundException("Structure %s not found".formatted(identifier));
        }

        return structure;
    }

    public Structure delete(String identifier) {
        Structure structure = get(identifier);
        structureRepository.delete(structure);
        return structure;
    }
}
