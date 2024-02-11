package nextstep.subway.api.domain.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.api.domain.model.entity.Station;
import nextstep.subway.api.domain.model.vo.Path;
import nextstep.subway.api.domain.operators.LineResolver;
import nextstep.subway.api.domain.operators.PathFinder;
import nextstep.subway.api.domain.operators.StationResolver;
import nextstep.subway.api.interfaces.dto.response.PathResponse;
import nextstep.subway.common.exception.PathNotValidException;

/**
 * @author : Rene Choi
 * @since : 2024/02/11
 */

@ExtendWith(MockitoExtension.class)
class SimplePathServiceTest {

	@InjectMocks
	private SimplePathService simplePathService;

	@Mock
	private StationResolver stationResolver;

	@Mock
	private PathFinder pathFinder;

	@Mock
	private LineResolver lineResolver;

	@Test
	@DisplayName("최단 경로 조회 성공 - 단순 mock으로 성공 케이스")
	void findShortestPath_Success() {
		// given
		Station sourceStation = new Station(1L, "SourceStation");
		Station targetStation = new Station(2L, "TargetStation");

		given(stationResolver.fetchOptional(1L)).willReturn(Optional.of(sourceStation));
		given(stationResolver.fetchOptional(2L)).willReturn(Optional.of(targetStation));
		given(lineResolver.fetchAll()).willReturn(new ArrayList<>());
		given(pathFinder.findShortestPath(eq(sourceStation), eq(targetStation), anyList())).willReturn(Path.of(Arrays.asList(sourceStation, targetStation), 10L));

		// when
		PathResponse result = simplePathService.findShortestPath(1L, 2L);

		// then
		assertThat(result.getStations()).hasSize(2);
		assertThat(result.getDistance()).isEqualTo(10);
	}

	@Test
	@DisplayName("출발역과 도착역이 동일할 때 최단 경로 조회 실패")
	void findShortestPath_Failure_SameSourceAndTarget() {
		// given
		long sourceId = 1L;

		// when & then
		assertThatThrownBy(() -> simplePathService.findShortestPath(sourceId, sourceId))
			.isInstanceOf(PathNotValidException.class)
			.hasMessageContaining("Source and target stations cannot be the same.");
	}
}
