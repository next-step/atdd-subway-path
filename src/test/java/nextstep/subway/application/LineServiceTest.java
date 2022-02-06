package nextstep.subway.application;

import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.application.dto.StationResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {

    private final String lineName = "2호선";
    private final String lineColor = "green";
    private final String upStationName = "강남역";
    private final String downStationName = "역삼역";
    private final int distance = 10;

    private Long upStationId;
    private Long downStationId;
    private Station upStation;
    private Station downStation;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        upStationId = stationRepository.save(new Station(upStationName)).getId();
        downStationId = stationRepository.save(new Station(downStationName)).getId();
        upStation = stationRepository.findById(upStationId).orElseThrow(IllegalStateException::new);
        downStation = stationRepository.findById(downStationId).orElseThrow(IllegalStateException::new);
    }

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        // given
        // when
        LineResponse lineResponse = lineService.saveLine(new LineRequest(lineName, lineColor, upStationId, downStationId, distance));
        List<String> stations = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineName),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineColor),
                () -> assertThat(stations).isEqualTo(Arrays.asList(upStationName, downStationName))
        );
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        // given
        Line line = new Line(lineName, lineColor);
        line.addSection(upStation, downStation, distance);
        lineRepository.save(line);

        // when
        LineResponse lineResponse = lineService.showLines().stream().findAny().get();
        List<String> stations = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineName),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineColor),
                () -> assertThat(stations).isEqualTo(Arrays.asList(upStationName, downStationName))
        );
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        Line line = lineRepository.save(new Line(lineName, lineColor));
        Long lineId = line.getId();

        // when
        String newLineName = "신분당선";
        String newLineColor = "red";
        lineService.updateLine(lineId, new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance));

        // then
        Line updated = lineRepository.findById(lineId).orElseThrow(IllegalStateException::new);
        assertAll(
                () -> assertThat(updated.getName()).isEqualTo(newLineName),
                () -> assertThat(updated.getColor()).isEqualTo(newLineColor)
        );
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        Line line = lineRepository.save(new Line(lineName, lineColor));
        Long lineId = line.getId();

        // when
        lineService.deleteLine(lineId);

        // then
        assertThat(lineRepository.findById(lineId).isPresent()).isFalse();
    }

    @DisplayName("지하철 노선 내 구간 추가")
    @Test
    void addSection() {
        // given
        Line line = lineRepository.save(new Line(lineName, lineColor));

        // when
        lineService.addSection(line.getId(), new SectionRequest(upStationId, downStationId, distance));

        // then
        assertThat(line.getStations()).isEqualTo(List.of(upStation, downStation));
    }

    @DisplayName("지하철 노선 내 구간 삭제")
    @Test
    void deleteSection() {
        // given
        Line line = lineRepository.save(new Line(lineName, lineColor));
        Station newStation = stationRepository.save(new Station("사당역"));
        line.addSection(upStation, downStation, distance);
        line.addSection(downStation, newStation, distance);

        // when
        lineService.deleteSection(line.getId(), newStation.getId());

        // then
        List<Station> stations = line.getStations();
        assertAll(
                () -> assertThat(stations).containsAll(List.of(upStation, downStation)),
                () -> assertThat(stations).doesNotContain(newStation)
        );
    }
}
