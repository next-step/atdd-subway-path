package nextstep.subway.api.domain.service;

import nextstep.subway.api.interfaces.dto.response.PathResponse;

/**
 * @author : Rene Choi
 * @since : 2024/02/09
 */
public interface PathService {
	PathResponse findShortestPath(Long source, Long target);
}
