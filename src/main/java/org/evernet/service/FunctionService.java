package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Function;
import org.evernet.repository.FunctionRepository;
import org.evernet.request.FunctionCreationRequest;
import org.evernet.request.FunctionUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FunctionService {

    private final FunctionRepository functionRepository;

    private final StructureService structureService;

    public Function create(String nodeIdentifier, String structureAddress, FunctionCreationRequest request, String creator) {
        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        if (functionRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, request.getIdentifier())) {
            throw new ClientException(String.format("Function %s already exists for structure %s on node %s", request.getIdentifier(), structureAddress, nodeIdentifier));
        }

        Function function = Function.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .inputDataFormat(request.getInputDataFormat())
                .inputDataSchema(request.getInputDataSchema())
                .outputDataFormat(request.getOutputDataFormat())
                .outputDataSchema(request.getOutputDataSchema())
                .creator(creator)
                .build();

        return functionRepository.save(function);
    }

    public List<Function> list(String nodeIdentifier, String structureAddress) {
        return functionRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public Function get(String identifier, String structureAddress, String nodeIdentifier) {
        Function function = functionRepository.findByIdentifierAndStructureAddressAndNodeIdentifier(identifier, structureAddress, nodeIdentifier);

        if (function == null) {
            throw new NotFoundException(String.format("Function %s not found for structure %s on node %s", identifier, structureAddress, nodeIdentifier));
        }

        return function;
    }

    public Function update(String identifier, FunctionUpdateRequest request, String structureAddress, String nodeIdentifier) {
        Function function = get(identifier, structureAddress, nodeIdentifier);

        if (request.getInputDataFormat() != null) {
            function.setInputDataFormat(request.getInputDataFormat());
        }

        function.setInputDataSchema(request.getInputDataSchema());

        if (request.getOutputDataFormat() != null) {
            function.setOutputDataFormat(request.getOutputDataFormat());
        }

        function.setOutputDataSchema(request.getOutputDataSchema());

        if (StringUtils.hasText(request.getDisplayName())) {
            function.setDisplayName(request.getDisplayName());
        }

        function.setDescription(request.getDescription());

        return functionRepository.save(function);
    }

    public Function delete(String identifier, String structureAddress, String nodeIdentifier) {
        Function function = get(identifier, structureAddress, nodeIdentifier);
        functionRepository.delete(function);
        return function;
    }

    public Boolean exists(String identifier, String structureAddress, String nodeIdentifier) {
        return functionRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, identifier);
    }
}
