package nextstep.subway.unit.path;

import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("경로 조회 컨트롤러 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class PathFinderServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @DisplayName("경로 조회 기능")
    @Test
    void findPath() {

    }
}