package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    LineService lineService;

    @Mock
    StationService stationService;

    @InjectMocks
    PathService pathService;

    TestObjectFactory testObjectFactory = new TestObjectFactory();

    @Test
    @DisplayName("실패_출발역과 도착역이 같은 경우")
    void fail_same_start_end() {
        Station 강남역 = testObjectFactory.역생성("강남역");
        Station 양재역 = testObjectFactory.역생성("양재역");

        assertThrows(IllegalArgumentException.class, () -> pathService.getPath(강남역.getId(), 양재역.getId()));
    }
}
