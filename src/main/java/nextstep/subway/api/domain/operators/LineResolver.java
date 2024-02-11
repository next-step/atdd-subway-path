package nextstep.subway.api.domain.operators;

import java.util.List;
import java.util.Optional;

import nextstep.subway.api.domain.model.entity.Line;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
public interface LineResolver {
	List<Line> fetchAll();

	Optional<Line> fetchOptional(Long id);

}
