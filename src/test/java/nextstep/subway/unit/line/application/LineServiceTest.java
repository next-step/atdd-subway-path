package nextstep.subway.unit.line.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.dto.SectionRequest;
import nextstep.subway.line.domain.model.Distance;
import nextstep.subway.line.domain.model.Line;
import nextstep.subway.line.domain.repository.LineRepository;
import nextstep.subway.station.domain.model.Station;
import nextstep.subway.station.domain.repository.StationRepository;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station upStation = stationRepository.save(new Station("초기 상행"));
        Station downStation = stationRepository.save(new Station("초기 하행"));
        Line line = lineRepository.save(new Line("초기 라인", "bg-red-500"));

        // when
        // lineService.addSection 호출
        SectionRequest request = SectionRequest.builder()
            .upStationId(upStation.getId())
            .downStationId(downStation.getId())
            .distance(new Distance(100))
            .build();
        lineService.addSection(line.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().toStations().size()).isEqualTo(2);
    }
}
