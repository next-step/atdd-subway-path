package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("최단 경로 관리 - 실객체")
@SpringBootTest
@Transactional
public class PathServiceTest {
    private static final int DEFAULT_DISTANCE = 5;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private PathService pathService;

    private Line 일호선;
    private Line 이호선;

    private Station 부평역;
    private Station 신도림역;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    void setUp() {
        부평역 = stationRepository.save(createStationEntity("부평역"));
        신도림역 = stationRepository.save(createStationEntity("신도림역"));
        강남역 = stationRepository.save(createStationEntity("강남역"));
        역삼역 = stationRepository.save(createStationEntity("역삼역"));
        삼성역 = stationRepository.save(createStationEntity("삼성역"));

        일호선 = lineRepository.save(createLineEntity("일호선", "red"));
        일호선.addSection(부평역, 신도림역, DEFAULT_DISTANCE);

        이호선 = lineRepository.save(createLineEntity("이호선", "grean"));
        이호선.addSection(신도림역, 강남역, DEFAULT_DISTANCE);
        이호선.addSection(강남역, 역삼역, DEFAULT_DISTANCE);
    }

    @DisplayName("부평역(1호선)에서 강남역(2호선) 최단 거리를 구한다")
    @Test
    void shortPath() {
        // given
        int sumDistance = DEFAULT_DISTANCE * 2;

        // when
        PathResponse pathResponse = pathService.shortPath(부평역.getId(), 강남역.getId());

        // then
        List<String> resultNames = getStationNames(pathResponse);

        assertAll(
                () -> assertThat(resultNames).containsExactly(부평역.getName(), 신도림역.getName(), 강남역.getName()),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(sumDistance)
        );
    }

    @DisplayName("동일한 출발지와 도착역으로 최단 경로 조회하면 예외 발생")
    @Test
    void sourceEqualsTargetException() {
        // when, then
        assertThatThrownBy(() -> pathService.shortPath(부평역.getId(), 부평역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 동일합니다.");
    }

    @DisplayName("연결이 안된 구간으로 요청하면 예외 발생")
    @Test
    void notConnectException() {
        // when, then
        assertThatThrownBy(() -> pathService.shortPath(부평역.getId(), 삼성역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private List<String> getStationNames(PathResponse pathResponse) {
        return pathResponse.getStations().stream()
                .map(stationResponse -> stationResponse.getName())
                .collect(Collectors.toList());
    }

    private Station createStationEntity(String name) {
        return new Station(name);
    }

    private Line createLineEntity(String name, String color) {
        return new Line(name, color);
    }
}
