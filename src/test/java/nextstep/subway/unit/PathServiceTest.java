package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.IdenticalSourceTargetNotAllowedException;
import nextstep.subway.exception.NonConnectedSourceTargetException;
import nextstep.subway.exception.StationNotFoundException;

@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 선릉역;
    private Station 한티역;
    private Line 이호선;
    private Line 분당선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대                         강남           선릉                 한티
     *  ● ────────── <2> ────────── ● -----X----- ● ───── <분당> ───── ●
     *  └───────┐                   │
     *         <3>                  │
     *          └─────●─────┐    <신분당>
     *            남부터미널  │       │
     *                     <3>      │
     *                      └────── ●
     *                             양재
     */
    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));
        선릉역 = stationRepository.save(new Station("선릉역"));
        한티역 = stationRepository.save(new Station("한티역"));

        이호선 = lineRepository.save(new Line("2호선", "green"));
        신분당선 = lineRepository.save(new Line("신분당선", "red"));
        분당선 = lineRepository.save(new Line("분당선", "yellow"));
        삼호선 = lineRepository.save(new Line("삼호선", "orange"));

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        분당선.addSection(new Section(분당선, 선릉역, 한티역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("출발역과 도착역 사이의 최단 경로를 조회한다.")
    @Test
    void findPath() {
        // when
        PathResponse response = pathService.findPath(교대역.getId(), 양재역.getId());

        // then
        List<String> stations = response.getStations().stream()
            .map(StationResponse::getName)
            .collect(Collectors.toList());

        assertAll(
            () -> assertThat(stations).containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName()),
            () -> assertThat(response.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역와 도착역이 같은 경로는 조회할 수 없다.")
    @Test
    void identicalSourceTarget() {
        // when & then
        assertThatThrownBy(() -> pathService.findPath(교대역.getId(), 교대역.getId()))
            .isInstanceOf(IdenticalSourceTargetNotAllowedException.class);
    }

    @DisplayName("출발역와 도착역은 연결되어 있어야 한다.")
    @Test
    void notConnectedSourceTarget() {
        // when & then
        assertThatThrownBy(() -> pathService.findPath(교대역.getId(), 한티역.getId()))
            .isInstanceOf(NonConnectedSourceTargetException.class);
    }

    @DisplayName("지하철 경로 조회 시, 출발역과 도착역은 모두 존재하는 역이어야 한다.")
    @Test
    void nonExistSourceTarget() {
        // when & then
        assertThatThrownBy(() -> pathService.findPath(교대역.getId(), 999L))
            .isInstanceOf(StationNotFoundException.class);
    }
}
