package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathHelper {
    public static Station 역_만들기(Long id){
        Station station = mock(Station.class);
        when(station.getId()).thenReturn(id);
        return station;
    }
}
