package atdd.station.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@UtilityClass
public class ObjectMapperUtils {
    private static final Logger logger = LoggerFactory.getLogger(ObjectMapperUtils.class);

    final static ObjectMapper mapper = new ObjectMapper();

    public String valueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.error("ObjectMapperUtils valueAsString JsonProcessingException", e);
        }
        return "";
    }

    public <T> Optional<T> readValue(String json, TypeReference<T> valueTypeRef) {
        try {
            return Optional.of(mapper.readValue(json, valueTypeRef));
        } catch (JsonProcessingException e) {
            logger.error("ObjectMapperUtils readValue JsonProcessingException", e);
        }

        return Optional.empty();
    }
}
