package nextstep.subway.unit.service;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        Station 교대역 = stationRepository.save(new Station("교대역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));

        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 역삼역.getId());
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 교대역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 5);

        Line 이호선 = lineRepository.save(new Line("2호선", "bg-red-600"));

        // when
        lineService.addSection(sectionRequest, 이호선.getId());

        // then
        Sections sections = 이호선.getSections();
        Station station = sections.getDownStationEndPoint();
        assertThat(station).isEqualTo(역삼역);
    }
}
