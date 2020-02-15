package atdd.station.converter;

import atdd.station.model.entity.Station;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;

public class StationConverter implements AttributeConverter<Station, String> {
    private static final Logger logger = LoggerFactory.getLogger(StationConverter.class);

    final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Station attribute) {
        try {
            mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Station to Json JsonProcessingException", e);
        }
        return null;
    }

    @Override
    public Station convertToEntityAttribute(String json) {
        try {
            return mapper.readValue(json, Station.class);
        } catch (JsonProcessingException e) {
            logger.error("Station to Json JsonProcessingException", e);
        }

        return null;
    }
}
