package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.FindPathException;
import nextstep.subway.fixture.ConstStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    private Station 강남역;
    private Station 신논현역;
    private Station 정자역;
    private Station 판교역;
    private Station 이매역;

    private Line 신분당선;
    private Line 분당선;

    @BeforeEach
    void setUp() {
        강남역 = createStation(ConstStation.강남역);
        신논현역 = createStation(ConstStation.신논현역);
        정자역 = createStation(ConstStation.정자역);
        판교역 = createStation(ConstStation.판교역);
        이매역 = createStation(ConstStation.이매역);

        신분당선 = createLine(Line.of("신분당선", "bg-red-600"));
        분당선 = createLine(Line.of("분당선", "bg-yellow-600"));

        신분당선.addSection(Section.of(신논현역, 강남역, 10));
        신분당선.addSection(Section.of(강남역, 판교역, 15));

        분당선.addSection(Section.of(판교역, 정자역, 20));
        분당선.addSection(Section.of(정자역, 이매역, 25));
    }

    @DisplayName("출발지와 목적지를 통해 경로를 조회한다.")
    @Test
    void findPathSourceToTarget() {
        PathResponse pathResponse = pathService.findPath(PathRequest.of(신논현역.getId(), 이매역.getId()));

        assertAll(
                () -> assertThat(pathResponse.getDistance()).isEqualTo(70),
                () -> assertThat(pathResponse.getStations()).extracting("name")
                        .containsExactly("신논현역", "강남역", "판교역", "정자역", "이매역")
        );
    }

    @DisplayName("출발지와 목적지가 같을 경우 에러를 뱉는다.")
    @Test
    void sameSourceAndTarget() {
        assertThatThrownBy(() -> pathService.findPath(PathRequest.of(신논현역.getId(), 신논현역.getId())))
                .isInstanceOf(FindPathException.class)
                .hasMessage("출발역과 도착역이 같을 수는 없습니다.");
    }

    private Line createLine(Line line) {
        return lineRepository.save(line);
    }

    private Station createStation(Station station) {
        return stationRepository.save(station);
    }

}
