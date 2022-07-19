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

    private Station 광교역;
    private Station 광교중앙역;
    private Station 상현역;

    @BeforeEach
    void setUp() {
        sut = new LineService(lineRepository, new StationService(stationRepository));

        광교역 = createStationStep("광교역");
        광교중앙역 = createStationStep("광교중앙역");
        상현역 = createStationStep("상현역");
    }

    @DisplayName("노선 추가")
    @Test
    void createLine() {
        // given
        var distance = 10;
        var lineRequest = new LineRequest("신분당선", "red", 광교역.getId(), 광교중앙역.getId(), distance);

        // when
        var lineResponse = sut.saveLine(lineRequest);

        // then
        var expectedResponse = new LineResponse(
                lineResponse.getId(),
                lineRequest.getName(),
                lineRequest.getColor(),
                List.of(
                        new StationResponse(광교역.getId(), 광교역.getName()),
                        new StationResponse(광교중앙역.getId(), 광교중앙역.getName())
                )
        );
        var line = lineRepository.findById(lineResponse.getId());
        assertAll(
                () -> assertThat(lineResponse).isEqualTo(expectedResponse),
                () -> assertThat(line).isNotEmpty(),
                () -> assertThat(line.get().getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(line.get().getColor()).isEqualTo(lineRequest.getColor()),
                () -> assertThat(line.get().getStations()).containsExactly(광교역, 광교중앙역)
        );
    }


    @DisplayName("모든 노선 조회")
    @Test
    void showLines() {
        // given
        var distance = 10;
        var line = createLineStep("신분당선", "red", 광교역, 광교중앙역, distance);

        // when
        var lineResponses = sut.showLines();

        // then
        var expectedResponses = List.of(
                new LineResponse(
                        line.getId(),
                        line.getName(),
                        line.getColor(),
                        List.of(
                                new StationResponse(광교역.getId(), 광교역.getName()),
                                new StationResponse(광교중앙역.getId(), 광교중앙역.getName())
                        )
                    )
        );
        assertThat(lineResponses).isEqualTo(expectedResponses);
    }

    @DisplayName("ID로 노선 조회")
    @Test
    void findLineById() {
        // given
        var distance = 10;
        var line = createLineStep("신분당선", "red", 광교역, 광교중앙역, distance);

        // when
        var lineResponse = sut.findById(line.getId());

        // then
        var expectedResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                List.of(
                        new StationResponse(광교역.getId(), 광교역.getName()),
                        new StationResponse(광교중앙역.getId(), 광교중앙역.getName())
                )
        );
        assertThat(lineResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("노선 업데이트")
    @Test
    void updateLine() {
        // given
        var distance = 10;
        var line = createLineStep("신분당선", "red", 광교역, 광교중앙역, distance);

        // when
        var updateRequest = new LineRequest("구분당선", "black", 광교역.getId(), 광교중앙역.getId(), distance);
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
        var line = createLineStep("신분당선", "red", 광교역, 광교중앙역, 10);

        // when
        sut.deleteLine(line.getId());

        // then
        assertThat(lineRepository.findById(line.getId())).isEmpty();
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        var distance = 10;
        var line = createLineStep("신분당선", "red", 광교역, 광교중앙역, distance);

        // when
        sut.addSection(line.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), distance));

        // then
        assertThat(line.getStations()).containsExactly(광교역, 광교중앙역, 상현역);
    }

    @DisplayName("역 ID 로 구간 삭제")
    @Test
    void deleteLineById() {
        // given
        var line = createLineStep("신분당선", "red", 광교역, 광교중앙역, 10);
        line.addSection(광교중앙역, 상현역, 10);

        // when
        sut.deleteSection(line.getId(), 상현역.getId());

        // then
        assertThat(line.getStations()).containsExactly(광교역, 광교중앙역);
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
