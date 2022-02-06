package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineRepository.findById(1L).orElseThrow(IllegalArgumentException::new);
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("양재역");
    }
}
