package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 구로역 = new Station("구로역");
        Station 신도림역 = new Station("신도림역");
        Station 영등포역 = new Station("영등포역");

        stationRepository.save(구로역);
        stationRepository.save(신도림역);
        stationRepository.save(영등포역);

        Line 일호선 = new Line("1호선", "blue");
        lineRepository.save(일호선);

        // when
        // lineService.addSection 호출
        Long 구로역_아이디 = stationRepository.findById(1L).orElseThrow(IllegalArgumentException::new).getId();
        Long 신도림역_아이디 = stationRepository.findById(2L).orElseThrow(IllegalArgumentException::new).getId();
        Long 영등포역_아이디 = stationRepository.findById(3L).orElseThrow(IllegalArgumentException::new).getId();

        Line line = lineRepository.findById(1L).orElseThrow(IllegalArgumentException::new);

        lineService.addSection(line.getId(), new SectionRequest(구로역_아이디, 신도림역_아이디, 10));
        lineService.addSection(line.getId(), new SectionRequest(신도림역_아이디, 영등포역_아이디, 15));

        // then
        // line.getSections 메서드를 통해 검증
        List<String> stationNames = line.getStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(stationNames).contains("구로역", "신도림역", "영등포역");
    }
}
