package atdd.station.converter;

import atdd.station.utils.ObjectMapperUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LongListConverter implements AttributeConverter<List<Long>, String> {
    @Override
    public String convertToDatabaseColumn(List<Long> stations) {
        return ObjectMapperUtils.valueAsString(stations);
    }

    @Override
    public List<Long> convertToEntityAttribute(String json) {
        Optional optional = ObjectMapperUtils.readValue(json, new TypeReference<List<Long>>() {
        });

        if (optional.isPresent())
            return (List<Long>) optional.get();

        return new ArrayList<>();
    }
}
