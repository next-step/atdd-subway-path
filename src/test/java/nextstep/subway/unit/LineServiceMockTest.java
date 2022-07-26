package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    public static final Long LINE_ID = 1L;
    public static final long FIRST_STATION_ID = 1L;
    public static final long SECOND_STATION_ID = 2L;
    public static final long THIRD_STATION_ID = 3L;
    public static final int DISTANCE = 10;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    Line line;

    @BeforeEach
    void setUp() {
        stubStation("강남역", FIRST_STATION_ID);
        stubStation("역삼역", SECOND_STATION_ID);
        stubLine();
    }

    @Test
    void addSection() {

        // when
        addSection(FIRST_STATION_ID, SECOND_STATION_ID);

        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void removeSection() {
        // given
        stubStation("선릉역", THIRD_STATION_ID);
        addSection(FIRST_STATION_ID, SECOND_STATION_ID);
        addSection(SECOND_STATION_ID, THIRD_STATION_ID);

        // when
        lineService.deleteSection(LINE_ID, THIRD_STATION_ID);

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void removeSection_fail() {
        // given
        stubStation("선릉역", THIRD_STATION_ID);
        addSection(FIRST_STATION_ID, SECOND_STATION_ID);
        addSection(SECOND_STATION_ID, THIRD_STATION_ID);

        // when
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> lineService.deleteSection(LINE_ID, SECOND_STATION_ID)
                );
    }

    private void stubStation(String 강남역, long firstStationId) {
        doReturn(new Station(강남역))
                .when(stationService)
                .findById(firstStationId);
    }

    private void stubLine() {
        line = new Line("2호선", "빨간색");
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(LINE_ID);
    }

    private void addSection(Long upStationId, Long downStationId) {
        lineService.addSection(LINE_ID, new SectionRequest(upStationId, downStationId, DISTANCE));
    }
}
