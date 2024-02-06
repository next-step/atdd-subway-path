package nextstep.subway.api.domain.operators;

import java.util.List;
import java.util.Optional;

import nextstep.subway.api.domain.model.entity.Station;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
public interface StationResolver {
	List<Station> fetchAll();

	Optional<Station> fetchOptional(Long id);
}
