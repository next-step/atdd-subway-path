package atdd.station.utils;

import atdd.station.exception.ErrorType;
import atdd.station.exception.SubwayException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class ObjectMapperUtils {
    private static final Logger logger = LoggerFactory.getLogger(ObjectMapperUtils.class);

    final static ObjectMapper mapper = new ObjectMapper();

    public String valueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.error("ObjectMapperUtils valueAsString JsonProcessingException", e);

            throw new SubwayException(ErrorType.OBJECT_MAPPER_ERROR);
        }
    }

    public <T> T readValue(String json, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(json, valueTypeRef);
        } catch (JsonProcessingException e) {
            logger.error("ObjectMapperUtils readValue JsonProcessingException", e);

            throw new SubwayException(ErrorType.OBJECT_MAPPER_ERROR);
        }
    }
}
