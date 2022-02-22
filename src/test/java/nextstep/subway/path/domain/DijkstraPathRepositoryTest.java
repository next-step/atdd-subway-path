package nextstep.subway.path.domain;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.exception.InvalidSourceTargetException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

class DijkstraPathRepositoryTest {

    private PathRepository pathRepository;

    @BeforeEach
    void setUp() {
        pathRepository = new DijkstraPathRepository(mock(LineRepository.class));
    }

    @DisplayName("출발역과 도착역이 같은 경우 InvalidSourceTargetException 이 발생합니다.")
    @Test
    void invalidSourceTarget() {
        // given
        Station 강남역 = new Station("강남역");


        // when, then
        assertThatExceptionOfType(InvalidSourceTargetException.class)
                .isThrownBy(() -> pathRepository.findShortestPath(강남역, 강남역));
    }
}
