package xyz.evernet.federation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FederationHandlerFactory {

    private final Map<Class<?>, FederationHandler<?>> handlerMap;

    @Autowired
    public FederationHandlerFactory(List<FederationHandler<?>> handlers) {
        handlerMap = new HashMap<>();
        for (FederationHandler<?> handler : handlers) {
            handlerMap.put(handler.getEventType(), handler);
        }
    }

    public FederationHandler<?> getHandler(Class<?> eventType) {
        return handlerMap.get(eventType);
    }
}
