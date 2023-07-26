package nextstep.subway.unit;

import nextstep.subway.line.repository.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.CreateSectionRequest;
import nextstep.subway.station.service.StationFindable;
import nextstep.subway.unit.fake.FakeLineRepository;
import nextstep.subway.unit.fake.FakeStationFindable;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.fixture.LineFixture.신분당선_ID;
import static nextstep.subway.unit.fixture.StationFixture.논현역_ID;
import static nextstep.subway.unit.fixture.StationFixture.신논현역_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class LineServiceTest {
    private final LineRepository lineRepository = new FakeLineRepository();
    private final StationFindable stationFindable = new FakeStationFindable();
    private final LineService lineService = new LineService(lineRepository, stationFindable);

    @Test
    void addSection() {
        // given
        CreateSectionRequest request = new CreateSectionRequest(
                신논현역_ID,
                논현역_ID,
                5L
        );

        // when
        Line result = lineService.addSection(신분당선_ID, request);

        // then
        assertThat(result.getSections().size()).isEqualTo(2);
    }
}
