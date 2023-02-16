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
    private Station 강남역;
    private Station 역삼역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("강남역"));
        이호선 = lineRepository.save(new Line("이호선", "green"));
    }

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 3));

        // then
        assertThat(이호선.getSections()).containsExactly(new Section(이호선, 강남역, 역삼역, 3));
    }
}
