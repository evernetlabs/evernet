package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.MessageTypeHandlerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageTypeHandlerService {

    private final MessageTypeHandlerRepository messageTypeHandlerRepository;

    private final MessageTypeService messageTypeService;

    private final TransmitterService transmitterService;

    private final ReceiverService receiverService;

}
