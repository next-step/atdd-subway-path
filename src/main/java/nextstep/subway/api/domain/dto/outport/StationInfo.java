package nextstep.subway.api.domain.dto.outport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

	public static StationInfo from(Station station) {
		return ModelMapperBasedObjectMapper.convert(station, StationInfo.class);
	}
}
