package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 구간의 대한 테스트")
@SpringBootTest
@Transactional
class LineSectionServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @DisplayName("노선의 구간을 생성한다.")
    @Test
    void addSection() {

        final Station 강남역 = createStation("강남역");
        final Station 잠실역 = createStation("잠실역");
        final Line saveLine = createLine("2호선", "green");

        final SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 잠실역.getId(), 10);
        lineService.addSection(saveLine.getId(), sectionRequest);

        final List<Station> stations = saveLine.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 잠실역)
        );
    }

    private Station createStation(final String station) {
        return stationRepository.save(new Station(station));
    }

    private Line createLine(final String name, final String color) {
        return lineRepository.save(new Line(name, color));
    }
}
