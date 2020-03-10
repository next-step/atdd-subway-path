package atdd.path.service;

import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.Line;
import atdd.path.domain.LineRepository;
import atdd.path.domain.Station;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static atdd.path.TestConstant.*;
import static atdd.path.TestConstant.TEST_LINE_4;
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
    void findStationsInPath(){
        //given
        Long startId = TEST_STATION_2.getId();   //역삼역
        Long endId = TEST_STATION_7.getId();   //양재시민의숲역
        List<LineResponseView> collect = LINES.stream()
                .map(it -> LineResponseView.of(it))
                .collect(Collectors.toList());
        given(lineService.showAll()).willReturn(collect);

        //when
        List<Station> stationsInPath = graphService.findStationsInPath(startId, endId);

        //then
        assertThat(stationsInPath.size()).isEqualTo(4);
        assertThat(stationsInPath.get(1)).isEqualTo(TEST_STATION);
    }
}
