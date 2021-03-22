package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private Station 을지로3가역;
    private Station 을지로입구역;
    private Station 시청역;
    private Station 충정로역;

    private Line 이호선;

    @BeforeEach
    public void setUp() {
        // given
        을지로3가역 = stationRepository.save(new Station("을지로3가역"));
        을지로입구역 = stationRepository.save(new Station("을지로입구역"));
        시청역 = stationRepository.save(new Station("시청역"));
        충정로역 = stationRepository.save(new Station("충정로역"));

        이호선 = lineRepository.save(new Line("이호선", "green", 을지로3가역, 시청역, 10));
    }

    @Test
    void addSection() {
        // when
        // lineService.addSection 호출
        lineService.addSection(이호선, 시청역, 충정로역, 5);

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> sections = 이호선.getSections();
        assertThat(sections.size()).isEqualTo(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(을지로3가역);
        assertThat(sections.get(0).getDownStation()).isEqualTo(시청역);
        assertThat(sections.get(1).getUpStation()).isEqualTo(시청역);
        assertThat(sections.get(1).getDownStation()).isEqualTo(충정로역);
    }


    @Test
    void addSectionBetweenSectionWithSameUpStation() {
        // when
        // lineService.addSection 호출
        lineService.addSection(이호선, 을지로3가역, 을지로입구역, 5);

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> sections = 이호선.getSections();
        assertThat(sections.size()).isEqualTo(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(을지로3가역);
        assertThat(sections.get(0).getDownStation()).isEqualTo(을지로입구역);
        assertThat(sections.get(1).getUpStation()).isEqualTo(을지로입구역);
        assertThat(sections.get(1).getDownStation()).isEqualTo(시청역);
    }

}
