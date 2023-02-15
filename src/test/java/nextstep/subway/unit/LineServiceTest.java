package nextstep.subway.unit;

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

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 서비스 단위 테스트")
@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station upStation;
    private Station downStation;
    private Line line;
    private final int distance = 10;

    @BeforeEach
    void setUp() {
        upStation = new Station("upStation");
        downStation = new Station("downStation");

        line = new Line("line", "color");
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
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
        final List<Section> sections = line.getSections().getOrderedSections();
        final Section savedSection = sections.get(0);

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(savedSection.getUpStation()).isEqualTo(upStation),
                () -> assertThat(savedSection.getDownStation()).isEqualTo(downStation),
                () -> assertThat(savedSection.getDistance()).isEqualTo(distance)
        );
    }

    @DisplayName("지하철 노선의 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        lineRepository.save(line);
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), distance));

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(line.getId(), downStation.getId());

        // then
        // line.hasEmptySection 메서드를 통해 검증
        assertThat(line.hasEmptySection()).isTrue();
    }
}
