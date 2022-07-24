package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    Line 신분당선;
    Station 강남역;
    Station 양재역;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        양재역 = new Station(2L, "양재역");
        신분당선 = new Line(1L, "신분당선", "red");

    }

    /**
     * Given 노선과 역을 mocking 하고
     * When 지하철 노선을 생성하면
     * Then 생성된 노선과 mocking 한 노선의 정보가 일치한다.
     */
    @DisplayName("노선 생성")
    @Test
    void saveLine() {
        // given
        강남역_mocking();
        양재역_mocking();
        신분당선_mocking();

        // given 신분당선 save mocking
        when(lineRepository.save(any())).thenReturn(신분당선);

        // when
        lineService.saveLine(new LineRequest(신분당선.getName(), 신분당선.getColor(), 강남역.getId(), 양재역.getId(), 10));

        // then
        Line line = lineRepository.findById(신분당선.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(line.getName()).isEqualTo(신분당선.getName());
    }

    /**
     * Given 노선과 역을 mocking 하고
     * When 구간을 추가하면
     * Then 해당 노선에 추가한 역들을 확인할 수 있다.
     */
    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        강남역_mocking();
        양재역_mocking();
        신분당선_mocking();

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // then
        assertThat(신분당선.getStations()).containsExactly(강남역, 양재역);
    }

    /**
     * Given 노선을 mocking 하고
     * When 노선 정보를 수정하면
     * Then 수정된 노선 정보가 일치한다.
     */
    @DisplayName("노선 수정")
    @Test
    void updateLine() {
        // given
        신분당선_mocking();

        // when
        String newName = "2호선";
        String newColor = "green";
        lineService.updateLine(신분당선.getId(), new LineRequest(newName, newColor, 강남역.getId(), 양재역.getId(),10));

        // then
        assertThat(신분당선.getName()).isEqualTo(newName);
        assertThat(신분당선.getColor()).isEqualTo(newColor);
    }

    /**
     * When 노선을 제거하면
     * Then 해당 노선이 제거되어 조회시 에러가 발생한다.
     */
    @DisplayName("노선 제거")
    @Test
    void deleteLine() {
        // when
        lineService.deleteLine(신분당선.getId());

        // then
        assertThatThrownBy(()->lineService.findLineResponse(신분당선.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private void 강남역_mocking() {
        when(stationService.findStation(강남역.getId())).thenReturn(강남역);
    }

    private void 양재역_mocking() {
        when(stationService.findStation(양재역.getId())).thenReturn(양재역);
    }

    private void 신분당선_mocking() {
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));
    }
}
