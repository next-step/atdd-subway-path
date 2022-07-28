package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("지하철 구간을 등록합니다.")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 서울대입구역 = createStation("서울대입구역");
        Station 낙성대역 = createStation("낙성대역");
        Line 이호선 = createLine("이호선", "br-red-600");

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(서울대입구역.getId(), 낙성대역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        LineResponse 이호선_결과값 = lineService.findById(이호선.getId());
        LineResponse 이호선_예상값 = createLineResponse(이호선, List.of(서울대입구역, 낙성대역));
        Assertions.assertThat(이호선_결과값)
                .isEqualTo(이호선_예상값);
    }

    @DisplayName("지하철 구간을 삭제합니다.")
    @Test
    void deleteSection() {
        // Given
        Station 서울대입구역 = createStation("서울대입구역");
        Station 낙성대역 = createStation("낙성대역");
        Station 사당역 = createStation("사당역");
        Line 이호선 = createLine("이호선", "br-red-600");

        lineService.addSection(이호선.getId(), new SectionRequest(서울대입구역.getId(), 낙성대역.getId(), 10));
        lineService.addSection(이호선.getId(), new SectionRequest(낙성대역.getId(), 사당역.getId(), 10));

        // When
        lineService.deleteSection(이호선.getId(), 사당역.getId());

        // Then
        LineResponse 이호선_결과값 = lineService.findById(이호선.getId());
        LineResponse 이호선_예상값 = createLineResponse(이호선, List.of(서울대입구역, 낙성대역));
        Assertions.assertThat(이호선_결과값)
                .isEqualTo(이호선_예상값);
    }

    private LineResponse createLineResponse(Line line, List<Station> stations) {
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), line.getDistance(), createStationResponses(stations));
        return lineResponse;
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        List<StationResponse> collect = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return collect;
    }

    private Station createStation(String stationName) {
        Station station = new Station(stationName);
        stationRepository.save(station);
        return station;
    }

    private Line createLine(String name, String color) {
        Line line = new Line(name, color);
        lineRepository.save(line);
        return line;
    }
}
