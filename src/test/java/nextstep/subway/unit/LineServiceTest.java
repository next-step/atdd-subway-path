package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.applicaion.LineService;
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

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private int distance;
    private Line line;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        this.distance = 10;
        this.line = new Line("2호선", "bg-red-500");
        this.upStation = new Station("강남역");
        this.downStation = new Station("역삼역");
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        lineRepository.save(line);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), distance));

        // then
        // line.getSections 메서드를 통해 검증
        Section section = line.getSections().get(0);
        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation),
                () -> assertThat(section.getDistance()).isEqualTo(distance)
        );
    }
}
