package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Paths;
import nextstep.subway.exception.NoPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.NoSuchElementException;

import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.분당선;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.신분당선;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.이호선;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathsTest {

    private Paths paths;

    @BeforeEach
    void setPaths() {
        Line 이호선 = 이호선();
        Line 신분당선 = 신분당선();
        Lines lines = new Lines(List.of(이호선, 신분당선));
        paths = Paths.from(lines);
    }


    @Test
    @DisplayName("최단경로 조회")
    void getShortestPath() {
        // given
        int expectedSize = 3;
        int expectedDistance = 15;

        // when
        Path path = paths.getShortestPath(역삼역().getId(), 양재역().getId());

        // then
        assertThat(path.getStations()).hasSize(expectedSize)
                .containsExactly(역삼역(), 강남역(), 양재역());
        assertThat(path.getDistance()).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("최단경로 조회 실패 - 존재하지 않는역 조회")
    void getShortestPathWithInvalidStationId() {
        // given

        // when
        Executable executable = () -> paths.getShortestPath(선릉역().getId(), 양재역().getId());

        // then
        assertThrows(NoSuchElementException.class, executable);
    }

    @Test
    @DisplayName("최단경로 조회 실패 - 출발역, 도착역 같은 경우")
    void getShortestPathWithSameStationIds() {
        // given

        // when
        Executable executable = () -> paths.getShortestPath(양재역().getId(), 양재역().getId());

        // then
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    @DisplayName("최단경로 조회 실패 - 출발역, 도착역이 연결되지 않은 경우")
    void getShortestPathWithDisconnected() {
        // given
        Line 이호선 = 이호선();
        Line 분당선 = 분당선();
        분당선.addSection(선릉역(), 양재역(), FIVE);
        Lines lines = new Lines(List.of(이호선, 분당선));
        Paths paths = Paths.from(lines);

        // when
        Executable executable = () -> paths.getShortestPath(강남역().getId(), 양재역().getId());

        // then
        assertThrows(NoPathException.class, executable);
    }

}