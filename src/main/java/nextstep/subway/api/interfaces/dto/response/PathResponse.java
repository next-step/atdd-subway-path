package nextstep.subway.api.interfaces.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.domain.model.vo.Path;
import nextstep.subway.common.mapper.ModelMapperBasedObjectMapper;

/**
 * @author : Rene Choi
 * @since : 2024/02/07
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class PathResponse {
	private List<StationResponse> stations;
	private Long distance;

	public static PathResponse from(Path path) {
		return ModelMapperBasedObjectMapper.convert(path, PathResponse.class);
	}
}
