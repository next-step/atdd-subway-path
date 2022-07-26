package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
        final Station 기흥역 = new Station("기흥역");
        final Station 신갈역 = new Station("신갈역");
        stationRepository.save(기흥역);
        stationRepository.save(신갈역);

        final Line line = new Line("분당선", "yellow");
        lineRepository.save(line);

        // when
        lineService.addSection(line.getId(), new SectionRequest(기흥역.getId(), 신갈역.getId(), 10));

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("기흥역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("신갈역");
    }

    @ParameterizedTest
    @CsvSource(value = {"에버라인:red:에버라인:red", "에버라인::에버라인:yellow", ":red:분당선:red"}, delimiter = ':')
    void updateLine(String lineName, String color, String expectLineName, String expectColor){
        // given
        final Line line = new Line(lineName, color);
        lineRepository.save(line);

        // when
        lineService.updateLine(line.getId(), LineRequest.builder().color(expectColor).name(expectLineName).build());

        // then
        assertThat(line.getName()).isEqualTo(expectLineName);
        assertThat(line.getColor()).isEqualTo(expectColor);
    }
}
