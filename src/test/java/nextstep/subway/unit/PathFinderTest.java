package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.exception.PathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.helper.fixture.LineFixture.*;
import static nextstep.subway.helper.fixture.SectionFixture.추가구간_엔티티;
import static nextstep.subway.helper.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PathFinderTest {

    List<Line> 모든_노선;

    PathFinder pathFinder;

    @BeforeEach
    void setup() {
        Line 이호선 = 이호선_엔티티(강남역_엔티티, 역삼역_엔티티); // distance 7
        Line 신분당선 = 신분당선_엔티티(논현역_엔티티, 신논현역_엔티티); // distance 10
        Section 신분당선_추가구간 = 추가구간_엔티티(강남역_엔티티, 논현역_엔티티); // distance 10
        신분당선.addSection(신분당선_추가구간);

        모든_노선 = List.of(이호선,신분당선);
    }

    @Test
    @DisplayName("최단경로를 찾아 그 사이 stations와 총 가중치(거리)를 반환한다.")
    void succeed() {
        // when
        Path path = pathFinder.findShortestPathAndItsDistance(모든_노선, 논현역_엔티티, 역삼역_엔티티);

        // then
        List<String> 실제_경로_역들 = path.getStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(실제_경로_역들).containsExactly("논현역", "강남역", "역삼역_엔티티");
        assertThat(path.getDistance()).isEqualTo(18);
    }

    @Test
    @DisplayName("경로를 찾지 못했을 때 에러가 발생한다.")
    void succeedButPathNotExist() {
        // given
        Line 삼호선 = 삼호선_엔티티(고속터미널역_엔티티, 신사역_엔티티);
        모든_노선.add(삼호선);

        // when, then
        assertThrows(
                PathException.class,
                () -> pathFinder.findShortestPathAndItsDistance(모든_노선, 고속터미널역_엔티티, 역삼역_엔티티),
                "should throw"
        );
    }

    @Test
    @DisplayName("존재하지 않는 역으로 경로 조회 시 에러가 발생한다.")
    void failForNotFoundStation() {
        // given
        Station 노선에_없는_역 = 삼성역_엔티티;

        // when, then
        assertThrows(
                PathException.class,
                () -> pathFinder.findShortestPathAndItsDistance(모든_노선, 노선에_없는_역, 역삼역_엔티티),
                "should throw"
        );
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 값으로 주어지는 경우 에러가 발생한다.")
    void failForSameSourceAndTargetStation() {
        assertThrows(
                PathException.class,
                () -> pathFinder.findShortestPathAndItsDistance(모든_노선, 역삼역_엔티티, 역삼역_엔티티),
                "should throw"
        );
    }
}
