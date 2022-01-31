package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private final Long 상행역Id = 1L;
    private final Long 하행역Id = 2L;
    private final Long 노선Id = 1L;
    private Line 노선;
    private LineRequest 노선요청;
    private Station 상행역;
    private Station 하행역;
    private LineRequest 노선수정요청;
    @BeforeEach
    void setUp() {
        // given
        노선 = Line.of(노선Id, "노선이름", "노선색상");
        노선요청 = LineRequest.of("노선이름", "노선색상");
        상행역 = Station.of(상행역Id, "상행역");
        하행역 = Station.of(하행역Id, "하행역");
    }

    @Test
    void deleteLine() {
        LineService lineService;

        when_노선을_아이디로_삭제한다: {
            lineService = new LineService(lineRepository, stationService);
            lineService.deleteLine(노선Id);
        }

        then_삭제되었다: {
            List<LineResponse> lineResponses = lineService.showLines();
            assertThat(lineResponses.size()).isEqualTo(0);
        }
    }

    @Test
    void updateLine() {
        given: {
            노선수정요청 = LineRequest.of("수정노선이름", "수정노선색상");
            when(lineRepository.findById(노선Id)).thenReturn(Optional.of(노선));
        }

        when_노선의_정보를_수정한다: {
            LineService lineService = new LineService(lineRepository, stationService);
            lineService.updateLine(노선Id, 노선수정요청);
        }

        then_수정되었다: {
            assertThat(노선.getName()).isEqualTo(노선수정요청.getName());
        }
    }

    @Test
    void showLines() {
        List<LineResponse> lineResponses;

        given: {
            when(lineRepository.findAll()).thenReturn(Arrays.asList(노선));
        }

        when_등록된_노선들을_조회한다: {
            LineService lineService = new LineService(lineRepository, stationService);
            lineResponses = lineService.showLines();
        }

        then_전체노선이_조회된다: {
            assertThat(lineResponses.size()).isEqualTo(1);
        }
    }

    @Test
    void saveLine() {
        LineResponse lineResponse;

        given: {
            when(lineRepository.save(any(Line.class))).thenReturn(노선);
        }

        when_노선을_등록한다: {
            LineService lineService = new LineService(lineRepository, stationService);
            lineResponse = lineService.saveLine(노선요청);
        }

        then_노선이_등록된다: {
            assertThat(lineResponse.getId()).isEqualTo(노선Id);
        }
    }

    @Test
    void addSection() {
        LineService lineService;
        given:{
            when(stationService.findById(상행역Id)).thenReturn(상행역);
            when(stationService.findById(하행역Id)).thenReturn(하행역);
            when(lineRepository.findById(노선Id)).thenReturn(Optional.of(노선));
        }

        when_노선에_구간을_등록한다: {
            lineService = new LineService(lineRepository, stationService);
            lineService.addSection(노선Id, SectionRequest.of(상행역Id, 하행역Id, 99));
        }


        then_노선에_구간이_등록된다: {
            LineResponse lineResponse = lineService.findById(노선Id);
            assertThat(lineResponse.getStations().size()).isEqualTo(2);
        }
    }
}
