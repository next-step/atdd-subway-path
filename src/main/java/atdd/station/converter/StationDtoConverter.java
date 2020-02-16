package atdd.station.converter;

import atdd.station.model.dto.StationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StationDtoConverter implements AttributeConverter<List<StationDto>, String> {
    private static final Logger logger = LoggerFactory.getLogger(LongListConverter.class);

    final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<StationDto> stations) {
        try {
            return mapper.writeValueAsString(stations);
        } catch (JsonProcessingException e) {
            logger.error("Long List to Json JsonProcessingException", e);
        }
        return null;
    }

    @Override
    public List<StationDto> convertToEntityAttribute(String json) {
        try {
            return Objects.isNull(json) ? new ArrayList<>() : mapper.readValue(json, new TypeReference<List<StationDto>>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Json to Long List JsonProcessingException", e);
        }

        return null;
    }
}
