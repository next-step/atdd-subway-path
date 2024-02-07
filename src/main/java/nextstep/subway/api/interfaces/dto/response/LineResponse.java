package nextstep.subway.api.interfaces.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.domain.dto.outport.StationInfo;
import nextstep.subway.api.domain.model.entity.Line;
import nextstep.subway.common.mapper.ModelMapperBasedObjectMapper;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineResponse {

	private Long id;
	private String name;
	private String color;
	private List<StationInfo> stations;

	public static LineResponse from(Line line) {
		LineResponse response = ModelMapperBasedObjectMapper.convert(line, LineResponse.class);
		response.setStations(parseStations(line));
		return response;
	}

	private static List<StationInfo> parseStations(Line line) {
		return line.parseStations().stream().map(StationInfo::from).collect(Collectors.toList());
	}
}
