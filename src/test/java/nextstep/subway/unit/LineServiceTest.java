package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@SpringBootTest
@Transactional
@DisplayName("노선에 대한 통합 테스트")
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("노선을 생성한다.")
    void addSection() {
        Station upStation = createStation("판교역");
        Station downStation = createStation("정자역");
        Line line = createLine("신분당선", "bg-900");

        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 10));

        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("판교역");
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo("정자역");
        assertThat(sections.get(0).getDistance()).isEqualTo(10);
    }

    private Station createStation(String name) {
        return stationRepository.save(new Station(name));
    }

    private Line createLine(String name, String color) {
        return lineRepository.save(new Line(name, color));
    }
}
