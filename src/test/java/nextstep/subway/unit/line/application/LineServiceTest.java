package nextstep.subway.unit.line.application;

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

import nextstep.subway.common.domain.model.exception.EntityNotFoundException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.dto.LineRequest;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.repository.LineRepository;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.repository.StationRepository;
import nextstep.subway.utils.DatabaseCleanup;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station upStation;
    private Station downStation;
    private Line line;

    private final  String color = "bg-red-500";
    private final Distance distance = new Distance(100);

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();

        upStation = stationRepository.save(new Station("초기 상행"));
        downStation = stationRepository.save(new Station("초기 하행"));
        line = lineRepository.save(new Line("초기 라인", "bg-red-500"));

        SectionRequest request = SectionRequest.builder()
            .upStationId(upStation.getId())
            .downStationId(downStation.getId())
            .distance(distance)
            .build();
        lineService.addSection(line.getId(), request);
    }

    @Test
    void addSection() {
        // then
        assertThat(line.getSections().toStations().size()).isEqualTo(2);
    }


    @Test
    void findById() {
        // when
        lineRepository.save(line);

        // then
        assertDoesNotThrow(() -> lineService.findById(1L));
    }

    @Test
    void findByIdWithStations() {
        // when
        lineRepository.save(line);

        // then
        assertDoesNotThrow(() -> lineService.findByIdWithStations(1L));
    }

    @DisplayName("findLine 호출시 Stations를 포함 한다.")
    @Test
    void findLine() {
        // when
        LineResponse lineResponse = lineService.findLine(1L);

        // then
        List<String> names = lineResponse.getStations()
                                         .stream()
                                         .map(StationResponse::getName)
                                         .collect(Collectors.toList());
        assertThat(names).containsExactly(upStation.getName(), downStation.getName());
    }

    @DisplayName("노선 저장 - 노선 정보를 같이 전달하지 않으면 노선만 저장한다.")
    @Test
    void saveLineCase1() {
        // when
        LineRequest request = LineRequest.builder()
            .name("새로운 노선")
            .color(color)
            .build();
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getLength()).isZero();
    }

    @DisplayName("노선 저장 - 노선 정보를 같이 전달하면 노선도 같이 저장한다.")
    @Test
    void saveLineCase2() {
        // when
        LineRequest request = LineRequest.builder()
            .name("새로운 노선")
            .upStationId(upStation.getId())
            .downStationId(downStation.getId())
            .color(color)
            .distance(distance)
            .build();
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getLength()).isNotZero();
    }

    @DisplayName("노선 목록 - 모든 노선 정보를 반환한다. 지하철역 정보를 포함하지 않는다.")
    @Test
    void showLines() {
        // when
        List<LineResponse> response = lineService.showLines();

        // then
        List<List<StationResponse>> allStations =
            response.stream()
                    .map(LineResponse::getStations)
                    .collect(Collectors.toList());
        assertThat(response.size()).isEqualTo(1);
        for (List<StationResponse> eachStations : allStations) {
            assertThat(eachStations).isNull();
        }
    }

    @Test
    void updateLine() {
        // then
        String changedName = "변경된 이름";
        String changedColor = "bg-red-500";
        LineRequest lineRequest = LineRequest.builder()
            .name(changedName)
            .color(changedColor)
            .build();
        lineService.updateLine(line.getId(), lineRequest);

        // when
        assertThat(line.getName()).isEqualTo(changedName);
        assertThat(line.getColor()).isEqualTo(changedColor);
    }

    @Test
    void deleteLine() {
        // when
        lineService.deleteLine(line.getId());

        // then
        assertThatThrownBy(() -> lineService.findById(line.getId()))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteSection() {
        // given
        Station newDownStation = stationRepository.save(new Station("하행"));
        SectionRequest request = SectionRequest.builder()
            .upStationId(downStation.getId())
            .downStationId(newDownStation.getId())
            .distance(distance)
            .build();
        lineService.addSection(line.getId(), request);

        // when
        lineService.deleteSection(line.getId(), newDownStation.getId());

        // then
        List<String> stationNames = lineService.findByIdWithStations(line.getId())
                                               .getStations()
                                               .stream()
                                               .map(Station::getName)
                                               .collect(Collectors.toList());
        assertThat(stationNames).containsExactly(upStation.getName(), downStation.getName());
    }
}
