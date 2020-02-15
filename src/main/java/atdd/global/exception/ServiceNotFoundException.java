package atdd.global.exception;

import lombok.Getter;

import java.util.Map;

import static java.util.Collections.emptyMap;

@Getter
public class ServiceNotFoundException extends RuntimeException {

    private final Map<String, Object> params;

    public ServiceNotFoundException(String message) {
        super(message);
        params = emptyMap();
    }

    public ServiceNotFoundException(String message, Map<String, Object> params) {
        super(message);
        this.params = params;
    }

}
