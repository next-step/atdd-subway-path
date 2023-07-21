package nextstep.subway.unit;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Station;
import nextstep.subway.facade.SectionFacade;
import nextstep.subway.fixture.unit.entity.LineFixture;
import nextstep.subway.fixture.unit.entity.StationFixture;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.request.SectionRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SectionFacadeTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionFacade sectionFacade;

    @Test
    void addSection() {
        // given
        Line line = lineRepository.save(LineFixture.of("1호선", "green"));
        Station 강남역 = stationRepository.save(StationFixture.of("강남역"));
        Station 양재역 = stationRepository.save(StationFixture.of("양재역"));
        Station 남영역 = stationRepository.save(StationFixture.of("남영역"));
        int distance = 10;

        line.addSection(강남역, 양재역, distance);

        SectionRequest sectionRequest = new SectionRequest(남영역.getId(), 양재역.getId(), distance);

        // when
        // lineService.addSection 호출
        sectionFacade.addSection(line.getId(), sectionRequest);

        // then
        Assertions.assertThat(line.getSections().getSections()).hasSize(2);
    }
}
