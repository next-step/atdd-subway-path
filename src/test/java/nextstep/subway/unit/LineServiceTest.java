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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = new Line("이호선", "green");
        Line line = lineRepository.save(이호선);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        List<String> namesOfStations = line.stations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(namesOfStations).containsExactly("강남역", "역삼역");
    }
}
