package atdd.station.converter;

import atdd.station.model.entity.Edge;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EdgeListConverter implements AttributeConverter<List<Edge>, String> {
    private static final Logger logger = LoggerFactory.getLogger(EdgeListConverter.class);

    final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Edge> stations) {
        try {
            return mapper.writeValueAsString(stations);
        } catch (JsonProcessingException e) {
            logger.error("Edge List to Json JsonProcessingException", e);
        }
        return null;
    }

    @Override
    public List<Edge> convertToEntityAttribute(String json) {
        try {
            return Objects.isNull(json) ? new ArrayList<>() : mapper.readValue(json, new TypeReference<List<Edge>>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Json to Edge List JsonProcessingException", e);
        }

        return null;
    }
}
