package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService sut;

    @BeforeEach
    void setUp() {
        sut = new LineService(lineRepository, new StationService(stationRepository));
    }

    @DisplayName("노선 추가")
    @Test
    void createLine() {
        // given
        var upStation = createStationStep("광교역");
        var downStation = createStationStep("광교중앙역");
        var distance = 10;
        var lineRequest = new LineRequest("신분당선", "red", upStation.getId(), downStation.getId(), distance);

        // when
        var lineResponse = sut.saveLine(lineRequest);

        // then
        var expectedResponse = new LineResponse(
                lineResponse.getId(),
                lineRequest.getName(),
                lineRequest.getColor(),
                List.of(
                        new StationResponse(upStation.getId(), upStation.getName()),
                        new StationResponse(downStation.getId(), downStation.getName())
                )
        );
        var line = lineRepository.findById(lineResponse.getId());
        assertAll(
                () -> assertThat(lineResponse).isEqualTo(expectedResponse),
                () -> assertThat(line).isNotEmpty(),
                () -> assertThat(line.get().getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(line.get().getColor()).isEqualTo(lineRequest.getColor()),
                () -> assertThat(line.get().getStations()).containsExactly(upStation, downStation)
        );
    }


    @DisplayName("모든 노선 조회")
    @Test
    void showLines() {
        // given
        var upStation = createStationStep("광교역");
        var downStation = createStationStep("광교중앙역");
        var distance = 10;
        var line = createLineStep("신분당선", "red", upStation, downStation, distance);

        // when
        var lineResponses = sut.showLines();

        // then
        var expectedResponses = List.of(
                new LineResponse(
                        line.getId(),
                        line.getName(),
                        line.getColor(),
                        List.of(
                                new StationResponse(upStation.getId(), upStation.getName()),
                                new StationResponse(downStation.getId(), downStation.getName())
                        )
                    )
        );
        assertThat(lineResponses).isEqualTo(expectedResponses);
    }

    @DisplayName("ID로 노선 조회")
    @Test
    void findLineById() {
        // given
        var upStation = createStationStep("광교역");
        var downStation = createStationStep("광교중앙역");
        var distance = 10;
        var line = createLineStep("신분당선", "red", upStation, downStation, distance);

        // when
        var lineResponse = sut.findById(line.getId());

        // then
        var expectedResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                List.of(
                        new StationResponse(upStation.getId(), upStation.getName()),
                        new StationResponse(downStation.getId(), downStation.getName())
                )
        );
        assertThat(lineResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("노선 업데이트")
    @Test
    void updateLine() {
        // given
        var upStation = createStationStep("광교역");
        var downStation = createStationStep("광교중앙역");
        var distance = 10;
        var line = createLineStep("신분당선", "red", upStation, downStation, distance);

        // when
        var updateRequest = new LineRequest("구분당선", "black", upStation.getId(), downStation.getId(), distance);
        sut.updateLine(line.getId(), updateRequest);

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(updateRequest.getName()),
                () -> assertThat(line.getColor()).isEqualTo(updateRequest.getColor())
        );
    }

    @DisplayName("ID로 노선 삭제")
    @Test
    void deleteLine() {
        // given
        var upStation = createStationStep("광교역");
        var downStation = createStationStep("광교중앙역");
        var distance = 10;
        var line = createLineStep("신분당선", "red", upStation, downStation, distance);

        // when
        sut.deleteLine(line.getId());

        // then
        assertThat(lineRepository.findById(line.getId())).isEmpty();
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        var upStation = createStationStep("광교역");
        var downStation = createStationStep("광교중앙역");
        var newStation = createStationStep("상현역");
        var distance = 10;
        var line = createLineStep("신분당선", "red", upStation, downStation, distance);

        // when
        sut.addSection(line.getId(), new SectionRequest(downStation.getId(), newStation.getId(), distance));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("역 ID 로 구간 삭제")
    @Test
    void deleteLineById() {
        // given
        var upStation = createStationStep("광교역");
        var downStation = createStationStep("광교중앙역");
        var newStation = createStationStep("상현역");
        var line = createLineStep("신분당선", "red", upStation, downStation, 10);
        line.addSection(downStation, newStation, 10);

        // when
        sut.deleteSection(line.getId(), newStation.getId());

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    private Station createStationStep(String name) {
        return stationRepository.save(new Station(name));
    }

    private Line createLineStep(String name, String color, Station upStation, Station downStation, Integer distance) {
        var line = lineRepository.save(new Line(name, color));
        line.addSection(upStation, downStation, distance);
        return line;
    }
}
