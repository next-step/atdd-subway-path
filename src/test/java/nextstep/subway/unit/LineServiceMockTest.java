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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    @DisplayName("노선 생성")
    @Test
    void saveLine() {
        // given
        강남역_mocking();
        양재역_mocking();
        신분당선_mocking();

        // given 신분당선 save mocking
        when(lineRepository.save(any())).thenReturn(신분당선);

        // when 신분당선 저장
        lineService.saveLine(new LineRequest(신분당선.getName(), 신분당선.getColor(), 강남역.getId(), 양재역.getId(), 10));

        // then 생성된 라인과 신분당선의 이름이 같은지 확인
        Line line = lineRepository.findById(신분당선.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(line.getName()).isEqualTo(신분당선.getName());
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        강남역_mocking();
        양재역_mocking();
        신분당선_mocking();

        // when 강남-양재 구간 추가
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // then 신분당선에 강남역, 양재역이 존재하는지 확인
        assertThat(신분당선.getStations()).containsExactly(강남역, 양재역);
    }

    @DisplayName("노선 수정")
    @Test
    void updateLine() {
        // given
        신분당선_mocking();

        // when 신분당선의 이름과 색을 수정
        String newName = "2호선";
        String newColor = "green";
        lineService.updateLine(신분당선.getId(), new LineRequest(newName, newColor, 강남역.getId(), 양재역.getId(),10));

        // then 이름과 색이 변경되었는지 확인
        assertThat(신분당선.getName()).isEqualTo(newName);
        assertThat(신분당선.getColor()).isEqualTo(newColor);
    }

    @DisplayName("노선 제거")
    @Test
    void deleteLine() {
        // when 신분당선 제거
        lineService.deleteLine(신분당선.getId());

        // then 신분당선이 제거되어 조회시 에러 발생
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
