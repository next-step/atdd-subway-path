package nextstep.subway.unit;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.SchemaInitSql;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.model.SectionCreateRequest;
import nextstep.subway.section.service.SectionManageService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;

@SchemaInitSql
@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionManageService sectionManageService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station stationA = new Station("stationA");
        Station stationB = new Station("stationB");

        long givenLineId = 10L;
        Line lineAB = new Line(givenLineId, "lineAB", "yellow", stationA, stationB, new Sections(new ArrayList<>()));

        stationA = stationRepository.save(stationA);
        stationB = stationRepository.save(stationB);

        lineAB = lineRepository.save(lineAB);

        // when
        // lineService.addSection 호출

        sectionManageService.create(lineAB.getId(), new SectionCreateRequest(stationA.getId(), stationB.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증

        Assertions.assertThat(lineRepository.findById(lineAB.getId()).get().getSections().isEmpty()).isFalse();
    }
}
