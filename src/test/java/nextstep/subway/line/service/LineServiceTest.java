package nextstep.subway.line.service;

import nextstep.subway.common.fixture.LineFactory;
import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.repository.domain.Line;
import nextstep.subway.line.service.dto.SectionCreateRequest;
import nextstep.subway.station.repository.StationRepository;
import nextstep.subway.station.repository.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
    @DisplayName("section 을 추가 할 수 있다.")
    void addSection() {
        // given
        final Station 강남역 = stationRepository.save(StationFactory.createStation("강남역"));
        final Station 선릉역 = stationRepository.save(StationFactory.createStation("선릉역"));
        final Station 역삼역 = stationRepository.save(StationFactory.createStation("역삼역"));
        final int 강남역_선릉역_구간_길이 = 10;
        final int 선릉역_역삼역_구간_길이 = 20;
        final Line line = lineRepository.save(LineFactory.createLine("이호선", "연두색", SectionFactory.createSection(강남역, 선릉역, 강남역_선릉역_구간_길이)));
        final SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(선릉역.getId(), 역삼역.getId(), 선릉역_역삼역_구간_길이);

        // when
        lineService.addSection(line.getId(), sectionCreateRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간_길이 + 선릉역_역삼역_구간_길이);
            softly.assertThat(line.getStations()).containsExactly(강남역, 선릉역, 역삼역);
        });
    }
}
