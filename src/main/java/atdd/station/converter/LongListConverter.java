package atdd.station.converter;

import atdd.utils.ObjectMapperUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.AttributeConverter;
import java.util.List;

public class LongListConverter implements AttributeConverter<List<Long>, String> {
    @Override
    public String convertToDatabaseColumn(List<Long> stations) {
        return ObjectMapperUtils.valueAsString(stations);
    }

    @Override
    public List<Long> convertToEntityAttribute(String json) {
        return ObjectMapperUtils.readValue(json, new TypeReference<List<Long>>() {
        });
    }
}
