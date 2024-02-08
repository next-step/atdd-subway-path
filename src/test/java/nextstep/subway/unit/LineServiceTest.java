package nextstep.subway.unit;

import nextstep.subway.domain.line.LineCommand;
import nextstep.subway.domain.line.LineInfo;
import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.line.entity.Section;
import nextstep.subway.domain.line.service.LineService;
import nextstep.subway.domain.line.service.SectionService;
import nextstep.subway.domain.station.entity.Station;
import nextstep.subway.infrastructure.line.LineRepository;
import nextstep.subway.infrastructure.line.dao.LineStore;
import nextstep.subway.infrastructure.station.StationRepository;
import nextstep.subway.interfaces.line.dto.LineRequest;
import nextstep.subway.unit.common.LineFixture;
import nextstep.subway.unit.common.StationFixture;
import org.junit.jupiter.api.BeforeEach;
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
    private LineStore lineStore;

    @Autowired
    private LineStore lineReader;

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionService sectionService;


    Station A역;
    Station B역;
    Station C역;

    @BeforeEach
    void setUpFixture() {
        A역 = stationRepository.save(new Station("A역"));
        B역 = stationRepository.save(new Station("B역"));
        C역 = stationRepository.save(new Station("C역"));
    }

    @Test
    void addSection() {
        // given
        LineRequest.Line AB라인요청 = LineFixture.Request.라인요청("AB라인", "빨강", A역.getId(), B역.getId(), 10L);
        Section AB구간 = lineStore.createSection(AB라인요청);
        Line AB라인 = lineStore.store(new Line(AB라인요청.getName(), AB라인요청.getColor(), AB구간));

        LineRequest.Section BC구간요청 = LineFixture.Request.구간요청(B역.getId(), C역.getId(), 5L);
        LineCommand.SectionAddCommand command = LineCommand.SectionAddCommand.of(AB라인.getId(), BC구간요청);

        // when
        sectionService.saveSection(command);

        // then
        LineInfo.Main actual = lineService.retrieveBy(AB라인.getId());
        assertThat(actual.getSections().size()).isEqualTo(2);
        assertThat(actual).usingRecursiveComparison().isEqualTo(LineInfo.Main.from(AB라인));
    }
}
