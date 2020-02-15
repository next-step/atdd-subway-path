package atdd.station.converter;

import atdd.station.model.entity.Station;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StationListConverter implements AttributeConverter<List<Station>, String> {
    private static final Logger logger = LoggerFactory.getLogger(StationListConverter.class);

    final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Station> stations) {
        try {
            mapper.writeValueAsString(stations);
        } catch (JsonProcessingException e) {
            logger.error("Station to Json JsonProcessingException", e);
        }
        return null;
    }

    @Override
    public List<Station> convertToEntityAttribute(String json) {
        try {
            return Objects.isNull(json) ? new ArrayList<>() : mapper.readValue(json, new TypeReference<List<Station>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Station to Json JsonProcessingException", e);
        }

        return null;
    }
}
