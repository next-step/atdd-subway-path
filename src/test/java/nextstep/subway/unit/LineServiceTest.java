package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        final Station 강남역 = createStation("강남역");
        final Station 역삼역 = createStation("역삼역");
        final Line line = createLine("2호선", "bg-green");

        // when
        // lineService.addSection 호출
        final SectionRequest sectionRequest = new SectionRequest();
        sectionRequest.setUpStationId(강남역.getId());
        sectionRequest.setDownStationId(역삼역.getId());
        sectionRequest.setDistance(3);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections())
                .contains(new Section(line, 강남역, 역삼역, sectionRequest.getDistance()));
    }

    @Test
    void addSectionOfNewUpStation() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        final Station 강남역 = createStation("강남역");
        final Station 역삼역 = createStation("역삼역");
        final Line line = createLine("2호선", "bg-green");

        // when
        // lineService.addSection 호출
        final SectionRequest sectionRequest = new SectionRequest();
        sectionRequest.setUpStationId(강남역.getId());
        sectionRequest.setDownStationId(역삼역.getId());
        sectionRequest.setDistance(3);
        lineService.addSection(line.getId(), sectionRequest);

        final Station 서초역 = createStation("서초역");
        final SectionRequest sectionRequest2 = new SectionRequest();
        sectionRequest2.setUpStationId(서초역.getId());
        sectionRequest2.setDownStationId(강남역.getId());
        sectionRequest2.setDistance(2);
        lineService.addSection(line.getId(), sectionRequest2);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections())
                .containsSequence(
                        new Section(line, 서초역, 강남역, sectionRequest2.getDistance()),
                        new Section(line, 강남역, 역삼역, sectionRequest.getDistance()));
    }

    private Line createLine(String name, String color) {
        return lineRepository.save(new Line(name, color));
    }

    private Station createStation(String name) {
        return stationRepository.save(new Station(name));
    }

    @Test
    void deleteSection() {
        // given
        final Station 강남역 = createStation("강남역");
        final Station 역삼역 = createStation("역삼역");
        final Station 선릉역 = createStation("선릉역");
        final Line line = createLine("2호선", "bg-green");
        final Section 강남_역삼_구간 = new Section(line, 강남역, 역삼역, 3);
        final Section 역삼_선릉_구간 = new Section(line, 역삼역, 선릉역, 2);
        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        // when
        lineService.deleteSection(line.getId(), 선릉역.getId());

        // then
        assertThat(line.getSections()).doesNotContain(역삼_선릉_구간);
    }

    @DisplayName("구간이 하나뿐인 노선에서 구건을 제거할 수 없다")
    @Test
    void failDeleteSection() {
        // given
        final Station 강남역 = createStation("강남역");
        final Station 역삼역 = createStation("역삼역");
        final Line line = createLine("2호선", "bg-green");
        final Section 강남_역삼_구간 = new Section(line, 강남역, 역삼역, 3);
        line.addSection(강남_역삼_구간);

        // when && then
        assertThatThrownBy(() -> lineService.deleteSection(line.getId(), 역삼역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 하나뿐인 노선에서 구간을 제거할 수 없습니다.");

    }

}
