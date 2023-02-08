package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
        Station station = stationRepository.save(new Station("지하철1"));
        Station station2 = stationRepository.save(new Station("지하철2"));

        Line line = lineRepository.save(new Line("지하철노선1", "bg-red-600"));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(station.getId(), station2.getId(), 5));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
