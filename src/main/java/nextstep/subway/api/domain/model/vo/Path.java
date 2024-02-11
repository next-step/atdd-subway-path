package nextstep.subway.api.domain.model.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Value;
import nextstep.subway.api.domain.model.entity.Station;

/**
 * @author : Rene Choi
 * @since : 2024/02/09
 */
@Value
@AllArgsConstructor(staticName = "of")
public class Path {
	List<Station> stations;

	Long distance;

	public int size() {
		return stations.size();
	}

}
