package atdd.path.service;

import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static atdd.path.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GraphServiceTest {
    public static final List<Line> LINES = Lists.list(TEST_LINE, TEST_LINE_2, TEST_LINE_3, TEST_LINE_4);

    @InjectMocks
    GraphService graphService;

    @Mock
    LineService lineService;

    @Test
    void findStationsInPath() {
        //given
        List<Line> LINES = Lists.list(TEST_LINE, TEST_LINE_2, TEST_LINE_3, TEST_LINE_4);

        Long startId = TEST_STATION_11.getId();
        Long endId = TEST_STATION_15.getId();
        List<LineResponseView> collect = LINES.stream()
                .map(it -> LineResponseView.of(it))
                .collect(Collectors.toList());
        given(lineService.showAll()).willReturn(collect);

        //when
        List<Station> stationsInPath = graphService.findStationsInShortestPath(startId, endId);

        //then
        assertThat(stationsInPath.size()).isEqualTo(6);
        assertThat(stationsInPath.get(1)).isEqualTo(TEST_STATION_12);
    }
}
