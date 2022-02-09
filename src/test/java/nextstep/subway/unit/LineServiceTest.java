package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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


    private static final String 상행역 = "상행역";
    private static final String 하행역 = "하행역";
    private static final String 새로운역 = "새로운역";

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        final Station upStation = stationRepository.save(new Station(상행역));
        final Station downStation = stationRepository.save(new Station(하행역));
        final Station newStation = stationRepository.save(new Station(새로운역));
        final Line line = lineRepository.save(new Line("1호선", "blue"));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 1));
        lineService.addSection(line.getId(), new SectionRequest(downStation.getId(), newStation.getId(), 1));

        // then
        // line.getSections 메서드를 통해 검증
        final Sections sections = line.sections();
        assertThat(sections.isEmpty()).isFalse();
    }
}
