package atdd.station.converter;

import atdd.station.controller.StationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.time.LocalTime;

// TODO 삭제
public class LocalTimeConverter implements AttributeConverter<LocalTime, String> {
    private static final Logger logger = LoggerFactory.getLogger(LocalTimeConverter.class);

    final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(LocalTime localTime) {

        try {
            mapper.writeValueAsString(localTime);
        } catch (JsonProcessingException e) {
            logger.error("LocalTime to Json JsonProcessingException", e);

        }

        return null;
    }

    @Override
    public LocalTime convertToEntityAttribute(String dbData) {
        return null;
    }
}
