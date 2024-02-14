package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.NoLineException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;

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
    void testAddSection_노선에_구간을_추가할_수_있다() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Station 잠실역 = new Station("잠실역");

        stationRepository.save(강남역);
        stationRepository.save(역삼역);
        stationRepository.save(선릉역);
        stationRepository.save(잠실역);

        Section section = new Section(강남역, 역삼역, 10);

        Line 이호선 = new Line("이호선", "초록색");
        lineRepository.save(이호선);
        SectionRequest sectionRequest = SectionRequest.from(section);

        // when
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        Line 이호선AfterAddSection = lineRepository.findById(이호선.getId())
                                                .orElseThrow(() -> new NoLineException(이호선.getId() + "에 해당하는 노선이 없습니다."));
        assertThat(이호선AfterAddSection.getSections().getLastSection()).isEqualTo(section);
    }

    @Test
    void testAddSection_노선이_없으면_예외를_반환한다() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Station 잠실역 = new Station("잠실역");

        stationRepository.save(강남역);
        stationRepository.save(역삼역);
        stationRepository.save(선릉역);
        stationRepository.save(잠실역);

        Section section = new Section(강남역, 역삼역, 10);
        Long lineId = 1L;
        String lineName = "이호선";
        String lineColor = "초록색";
        Line 이호선 = new Line(lineId,lineName, lineColor);
        SectionRequest sectionRequest = SectionRequest.from(section);

        // when
        assertThatThrownBy(
            () -> lineService.addSection(lineId, sectionRequest))
            .isInstanceOf(NoLineException.class);
    }
}
