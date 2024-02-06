package nextstep.subway.api.domain.dto.outport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.domain.model.entity.Section;
import nextstep.subway.api.domain.model.entity.Station;
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
public class StationInfo {
	Long id;
	String name;

	public static StationInfo fromUpStation(Section section) {
		return StationInfo.builder()
			.id(section.fetchUpStationId())
			.name(section.fetchUpStationName())
			.build();
	}

	public static StationInfo fromDownStation(Section section) {
		return StationInfo.builder()
			.id(section.fetchDownStationId())
			.name(section.fetchDownStationName())
			.build();
	}

	public static StationInfo from(Station station){
		return ModelMapperBasedObjectMapper.convert(station, StationInfo.class);
	}
}
