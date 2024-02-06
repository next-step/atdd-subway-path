package nextstep.subway.common.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
public class ModelMapperBasedObjectMapper {

	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.addConverter(ModelMapperBasedObjectMapper::convertLocalDateTime);
		modelMapper.getConfiguration()
			.setMatchingStrategy(MatchingStrategies.STRICT);
	}

	private static LocalDateTime convertLocalDateTime(MappingContext<Object, Object> context) {
		return context.getSource() == null ? null : (LocalDateTime)context.getSource();
	}

	public static <T, U> U convert(T from, Class<U> toClass) {
		return modelMapper.map(from, toClass);
	}

	public static <T, U> List<U> convert(List<T> fromList, Class<U> toClass) {
		return fromList.stream()
			.map(item -> convert(item, toClass))
			.collect(Collectors.toList());
	}

	public static <T, U> U updateWithNonNullValues(T source, U destination) {
		modelMapper.map(source, destination);
		return destination;
	}
}
