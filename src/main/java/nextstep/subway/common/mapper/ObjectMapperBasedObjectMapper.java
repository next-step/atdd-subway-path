package nextstep.subway.common.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
public class ObjectMapperBasedObjectMapper {

	private static final ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public static <T, U> U convert(T from, Class<U> to) {
		try {
			String json = objectMapper.writeValueAsString(from);
			return objectMapper.readValue(json, to);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Object mapping failed", e);
		}
	}

	public static <T, U> List<U> convert(List<T> from, Class<U> to) {
		return from.stream()
			.map(each -> convert(each, to))
			.collect(Collectors.toList());
	}

	public static <T, U> U updateWithNonNullValues(T source, U destination) {
		try {
			return objectMapper.updateValue(destination, source);
		} catch (IllegalArgumentException | JsonMappingException e) {
			throw new RuntimeException("Object update failed", e);
		}
	}

}



