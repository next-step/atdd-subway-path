package atdd.station.converter;

import atdd.station.model.dto.LineDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineDtoListConverter implements AttributeConverter<List<LineDto>, String> {
    private static final Logger logger = LoggerFactory.getLogger(LineDtoListConverter.class);

    final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<LineDto> stations) {
        try {
            return mapper.writeValueAsString(stations);
        } catch (JsonProcessingException e) {
            logger.error("LineDto List to Json JsonProcessingException", e);
        }
        return null;
    }

    @Override
    public List<LineDto> convertToEntityAttribute(String json) {
        try {
            return Objects.isNull(json) ? new ArrayList<>() : mapper.readValue(json, new TypeReference<List<LineDto>>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Json to LineDto List JsonProcessingException", e);
        }

        return null;
    }
}
