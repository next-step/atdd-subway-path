package nextstep.subway.unit.path;

import nextstep.subway.applicaion.dto.path.PathFinderResponse;
import nextstep.subway.applicaion.exception.domain.PathFinderException;
import nextstep.subway.applicaion.service.PathFinderService;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Optional.*;

@DisplayName("경로 조회 서비스 계층 단위 테스트")
@SpringBootTest
@Transactional
public class PathFinderServiceTest {

    private Station 교대역 = new Station("교대역");
    private Station 강남역 = new Station("강남역");
    private Station 양재역 = new Station("양재역");
    private Station 남부터미널역 = new Station("남부터미널역");
    private Line 이호선 = new Line("2호선", "green", 교대역, 강남역, 10);
    private Line 신분당선 = new Line("신분당선", "red", 강남역, 양재역, 10);
    private Line 삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2);

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private PathFinderService pathFinderService;

    @BeforeEach
    void setUp() {
        setUpStation();
        setUpLine();
    }

    @DisplayName("조회 경로 시 출발역과 도착역이 같은 경우 400 예외가 발생한다.")
    @Test
    void sameSourceAndTarget() {
        // when
        PathFinderException exception = Assertions.assertThrows(PathFinderException.class, () -> {
            pathFinderService.findPath(교대역.getId(), 교대역.getId()); });

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }


    @DisplayName("조회 경로 시 출발역과 도착역이 연결되지 않은 경우 400 예외가 발생한다.")
    @Test
    void noPath() {
        // given
        Station 구디역 = new Station("구디역");
        Station 봉천역 = new Station("봉천역");
        Line 예외_노선 = new Line("예외_노선", "black", 구디역, 봉천역, 5);
        setUpNoPath(구디역, 봉천역, 예외_노선);

        // when
        PathFinderException exception = Assertions.assertThrows(PathFinderException.class, () -> {
            pathFinderService.findPath(구디역.getId(), 교대역.getId()); });

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @DisplayName("존재하지 않는 출발역 혹은 도착역을 조회할 시 400 예외가 발생한다.")
    @Test
    void notSavedSourceAndTarget() {
        // given
        Station 구디역 = new Station();
        stationRepository.save(구디역);

        // when
        PathFinderException exception = Assertions.assertThrows(PathFinderException.class, () -> {
            pathFinderService.findPath(교대역.getId(), 구디역.getId()); });

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    private void setUpStation() {
        stationRepository.save(교대역);
        stationRepository.save(강남역);
        stationRepository.save(양재역);
        stationRepository.save(남부터미널역);
    }

    private void setUpLine() {
        lineRepository.save(이호선);
        lineRepository.save(신분당선);
        lineRepository.save(삼호선);
    }

    private void setUpNoPath(Station 구디역, Station 봉천역, Line 예외_노선) {
        stationRepository.save(구디역);
        stationRepository.save(봉천역);
        lineRepository.save(예외_노선);
    }
}