package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        final Station 강남역 = new Station("강남역");
        final Station 서초역 = new Station("서초");
        stationRepository.save(강남역);
        stationRepository.save(서초역);
        final Line line = new Line("강남역 노선", "Green", 강남역, 서초역, 10);
        lineRepository.save(line);


        // when
        // lineService.addSection 호출
        final Station 방배역 = new Station("방배역");
        lineService.addSection(line, 서초역, 방배역, 15);

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> sections = line.getSections();
        final Set<Station> stations = sections.stream()
                .map(section -> Arrays.asList(section.getDownStation(), section.getUpStation()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertThat(stations).contains(방배역);
    }
}
