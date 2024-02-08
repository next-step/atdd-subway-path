package nextstep.subway.unit;

import nextstep.subway.domain.line.LineCommand;
import nextstep.subway.domain.line.LineInfo;
import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.line.entity.Section;
import nextstep.subway.domain.line.service.LineService;
import nextstep.subway.domain.line.service.SectionService;
import nextstep.subway.domain.station.entity.Station;
import nextstep.subway.domain.station.service.StationService;
import nextstep.subway.infrastructure.line.LineRepository;
import nextstep.subway.infrastructure.line.dao.LineReader;
import nextstep.subway.infrastructure.line.dao.LineStore;
import nextstep.subway.interfaces.line.dto.LineRequest;
import nextstep.subway.unit.common.LineFixture;
import nextstep.subway.unit.common.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineReader lineReader;
    @Mock
    private LineStore lineStore;
    @InjectMocks
    SectionService sectionService;
    @InjectMocks
    LineService lineService;

    Station A역;
    Station B역;
    Station C역;

    Section AB구간;
    Section BC구간;

    @BeforeEach
    void setUpFixture() {
        A역 = StationFixture.Entity.랜덤역생성();
        B역 = StationFixture.Entity.랜덤역생성();
        C역 = StationFixture.Entity.랜덤역생성();

        AB구간 = LineFixture.Entity.구간생성(A역,B역,2L);
        BC구간 = LineFixture.Entity.구간생성(B역,C역,3L);
    }

    @Test
    void addSection() {
        // given
        Line AB라인 = LineFixture.Entity.라인생성("AB라인", "빨강", AB구간);
        LineRequest.Section BC구간요청 = LineFixture.Request.구간요청(B역.getId(), C역.getId(), BC구간.getDistance());
        LineCommand.SectionAddCommand command = LineCommand.SectionAddCommand.of(AB라인.getId(), BC구간요청);
        when(lineReader.readBy(AB라인.getId())).thenReturn(AB라인);
        when(lineStore.createSection(command)).thenReturn(BC구간);

        // when
        sectionService.saveSection(command);

        // then
        LineInfo.Main actual = lineService.retrieveBy(AB라인.getId());
        assertThat(actual.getSections().size()).isEqualTo(2);
        assertThat(actual).usingRecursiveComparison().isEqualTo(LineInfo.Main.from(AB라인));

    }
}
