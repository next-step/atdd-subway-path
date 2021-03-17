package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNonExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("지하철 경로 조회 비즈니스 로직 단위 테스트")
@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private PathService pathService;

    private Station savedStationGangNam;
    private Station savedStationYangJae;
    private Station savedStationGyoDae;
    private Station savedStationNambuTerminal;

    private LineRequest lineNewBunDang;
    private LineRequest line2Request;
    private LineRequest line3Request;

    @BeforeEach
    void setUp() {
        savedStationGangNam = stationRepository.save(new Station("강남역"));
        savedStationYangJae = stationRepository.save(new Station("양재역"));
        savedStationGyoDae = stationRepository.save(new Station("교대역"));
        savedStationNambuTerminal = stationRepository.save(new Station("남부터미널역"));

        lineNewBunDang = new LineRequest("신분당선", "bg-red-600", savedStationGangNam.getId(), savedStationYangJae.getId(), 10);
        lineService.saveLine(lineNewBunDang);

        line2Request = new LineRequest("2호선", "bg-green-600", savedStationGyoDae.getId(), savedStationGangNam.getId(), 10);
        lineService.saveLine(line2Request);

        line3Request = new LineRequest("3호선", "bg-orange-600", savedStationGyoDae.getId(), savedStationNambuTerminal.getId(), 5);
        LineResponse line3Response = lineService.saveLine(line3Request);
        lineService.addSectionToLine(line3Response.getId(), createSectionRequest(savedStationNambuTerminal, savedStationYangJae, 3));
    }

    @Test
    @DisplayName("지하철 최단 경로 조회")
    void findShortestPathStation() {
        // given
        long source = savedStationGangNam.getId();
        long target = savedStationNambuTerminal.getId();

        // when
        PathResponse pathResponse = pathService.getShortestPath(source, target);

        // then
        assertThat(pathResponse.getStations()).hasSize(3);
        assertThat(pathResponse.getStation).containsAll(savedStationGangNam, savedStationGyoDae, savedStationNambuTerminal);
        assertThat(pathResponse.getDistance()).isEqualTo(15);
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경우 예외 발생")
    void notEqualsSourceAndTarget() {
        // given
        long source = savedStationGangNam.getId();
        long target = savedStationGangNam.getId();

        // when & then
        assertThatExceptionOfType(SameStationPathSearchException.class)
                .isThrownBy(() -> pathService.getShortestPath(source, target));
    }

    @Test
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 예외 발생")
    void notConnectedSourceAndTarget() {
        // given
        Station savedStationMyeongDong = stationRepository.save(new Station("명동역"));

        long source = savedStationGangNam.getId();
        long target = savedStationMyeongDong.getId();

        // when & then
        assertThatExceptionOfType(NotConenctedPathException.class)
                .isThrownBy(() -> {
                    pathService.getShortestPath(source, target);
                });
    }

    @Test
    @DisplayName("존재하지 않는 출발역, 도착역을 조회할 경우 예외 발생")
    void findNotExistSourceAndTarget() {
        // given
        long source = 100L;
        long target = 101L;

        // when & then
        assertThatExceptionOfType(StationNonExistException.class)
                .isThrownBy(() -> pathService.getShortestPath(source, target));
    }

    private SectionRequest createSectionRequest(Station upStation, Station downStation, int distance) {
        return new SectionRequest(upStation.getId(), downStation.getId(), distance);
    }
}
