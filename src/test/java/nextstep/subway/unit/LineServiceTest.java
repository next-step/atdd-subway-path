package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("구간 서비스 테스트")
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    Line line;
    Station firstStation;
    Station secondStation;
    Section section;

    @BeforeEach
    void init() {
        line = new Line("신분당선", "red");
        lineRepository.save(line);

        firstStation = new Station("강남역");
        secondStation = new Station("판교역");
        stationRepository.save(firstStation);
        stationRepository.save(secondStation);


    }
    @Test
    void addSection() {
        section = new Section(line, firstStation, secondStation, 10);

        SectionRequest sectionRequest = new SectionRequest(firstStation.getId(), secondStation.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        LineResponse response = lineService.findById(line.getId());
        assertThat(response.getStations()).hasSize(2);
    }
}
