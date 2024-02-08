package nextstep.subway.api.interfaces.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.domain.dto.outport.StationInfo;

/**
 * @author : Rene Choi
 * @since : 2024/02/07
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathResponse {
	private List<StationInfo> stations;
	private Long distance;
}
