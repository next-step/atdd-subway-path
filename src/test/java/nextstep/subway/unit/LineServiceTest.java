package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineSectionResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.SectionRequest;
import nextstep.subway.line.section.SectionResponse;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private final Station 강남역 = new Station("강남역");
    private final Station 역삼역 = new Station("역삼역");
    private final Station 선릉역 = new Station("선릉역");
    private final Line 강남선 = new Line("강남선", "red");

    @BeforeEach
    public void setUp() {
        stationRepository.saveAll(List.of(강남역, 역삼역, 선릉역));
        lineRepository.save(강남선);
    }

    @DisplayName("구간 1개 추가")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        // when
        // lineService.addSection 호출
        SectionResponse response = lineService.addSection(강남선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10L));
        System.out.println(response.toString());

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(강남선.getSections().get()).hasSize(1);
    }

    @DisplayName("마지막 구간 1개 제거")
    @Test
    void deleteSection() {
        // given
        Section 강남_삼성 = new Section(강남선, 강남역, 역삼역, 10L);
        Section 삼성_선릉 = new Section(강남선, 역삼역, 선릉역, 10L);
        강남선.addSection(강남_삼성);
        강남선.addSection(삼성_선릉);

        // when
        lineService.deleteSection(강남선.getId(), 선릉역.getId());

        // then
        assertThat(강남선.getSections().get()).hasSize(1);
    }
}
