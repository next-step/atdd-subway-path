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

import static org.assertj.core.api.BDDAssertions.then;

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
        Line savedLine = lineRepository.save(new Line("2호선", "green"));
        Station savedUpStation = stationRepository.save(new Station("강남역"));
        Station savedDownStation = stationRepository.save(new Station("건대입구역"));
        SectionRequest request = new SectionRequest(savedUpStation.getId(), savedDownStation.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(savedLine.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        Line findLine = lineRepository.findById(savedLine.getId()).get();
        then(findLine.getSections()).hasSize(1);
        then(findLine.getStations()).extracting("name").containsExactly("강남역", "건대입구역");
    }
}
