package nextstep.subway.unit;

import nextstep.subway.path.service.CannotFindPathException;
import nextstep.subway.path.service.PathService;
import nextstep.subway.path.presentation.PathsResponse;
import nextstep.subway.path.domain.dto.StationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PathsServiceMockTest {

    @Mock
    private PathService pathService;


    @DisplayName("출발역으로부터 도착역까지의 경로에 있는 역 목록이 조회된다")
    @Test
    void shouldSearchPathFromStartToEnd() {

        // given
        StationDto 시청 = new StationDto(1, "시청");
        StationDto 을지로입구 = new StationDto(2, "을지로입구");
        StationDto 을지로4가 = new StationDto(3, "을지로4가");
        PathsResponse pathsResponse = new PathsResponse(15, List.of(시청, 을지로입구, 을지로4가));
        // when
        when(pathService.searchPath(1, 3))
                .thenReturn(pathsResponse);

        // then
        assertThat(pathService.searchPath(1, 3).getStationDtoList())
                .containsExactly(시청, 을지로입구, 을지로4가);
    }

    @Test
    @DisplayName("경로 탐색이 불가능한 요청은 실패한다")
    public void shouldFailIfCannotFindPath() {

        // when
        when(pathService.searchPath(Integer.MAX_VALUE, Integer.MAX_VALUE))
                .thenThrow(CannotFindPathException.class);

        // then
        assertThrows(CannotFindPathException.class, () -> pathService.searchPath(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

}
