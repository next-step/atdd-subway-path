package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.constants.ErrorConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("지하철 구간 서비스 Mock 단위 테스트")
public class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @BeforeEach
    void setUp() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("신분당선", "RED")));
        when(stationService.findById(1L)).thenReturn(new Station("강남역"));
        when(stationService.findById(2L)).thenReturn(new Station("양재역"));
    }

    @Test
    @DisplayName("지하철 구간 추가")
    void addSection() {
        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(1L);
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("신분당선"),
                () -> assertThat(line.getColor()).isEqualTo("RED")
        );
    }

    @Test
    @DisplayName("기존 구간 길이보다 크거나 같은 역 사이 새로운 역을 갖는 구간 추가")
    void addSection_moreThenDistance() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("정자역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when
        // then
        assertThatThrownBy(() -> lineService.addSection(1L, new SectionRequest(3L, 2L, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MORE_THEN_DISTANCE);
    }

    @Test
    @DisplayName("상행역과 하행역 모두 이미 등록된 구간 추가")
    void addSection_alreadyEnrollStation() {
        // given
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when
        // then
        assertThatThrownBy(() -> lineService.addSection(1L, new SectionRequest(1L, 2L, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ALREADY_ENROLL_STATION);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 등록되지 않은 구간 추가")
    void addSection_notEnrollStation() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("정자역"));
        when(stationService.findById(4L)).thenReturn(new Station("판교역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when
        // then
        assertThatThrownBy(() -> lineService.addSection(1L, new SectionRequest(3L, 4L, 6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_ENROLL_STATION);
    }

    @Test
    @DisplayName("기존 구간 사이 역을 갖는 구간 추가")
    void addSection_middleStation() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("정자역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when
        lineService.addSection(1L, new SectionRequest(3L, 2L, 6));

        // then
        Line line = lineService.findLineById(1L);
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("강남역", "정자역", "양재역");
    }

    @Test
    @DisplayName("하행 종점역을 상행역으로 갖는 구간 추가")
    void addSection_lastStation() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("정자역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when
        lineService.addSection(1L, new SectionRequest(2L, 3L, 6));

        // then
        Line line = lineService.findLineById(1L);
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("강남역", "양재역", "정자역");
    }

    @Test
    @DisplayName("상행 종점역을 하행역으로 갖는 구간 추가")
    void addSection_frontStation() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("판교역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when
        lineService.addSection(1L, new SectionRequest(3L, 1L, 6));

        // then
        Line line = lineService.findLineById(1L);
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("판교역", "강남역", "양재역");
    }

    @Test
    @DisplayName("등록된 구간이 하나 이하인 노선 구간 삭제")
    void removeSection_lessThanOneSection() {
        // given
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when
        // then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LESS_THAN_ONE_SECTION);
    }

    @Test
    @DisplayName("등록되지 않은 구간 삭제")
    void removeSection_notFoundStation() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("정자역"));
        when(stationService.findById(4L)).thenReturn(new Station("판교역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(3L, 2L, 6));

        // when
        // then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_FOUND_STATION);
    }

    @Test
    @DisplayName("하행 종점역 삭제")
    void removeSection_lastStation() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("정자역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(3L, 2L, 6));

        // when
        lineService.deleteSection(1L, 2L);

        // then
        Line line = lineService.findLineById(1L);
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("강남역", "정자역");
    }

    @Test
    @DisplayName("상행 종점역 삭제")
    void removeSection_frontStation() {
        // given
        when(stationService.findById(3L)).thenReturn(new Station("정자역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(3L, 2L, 6));

        // when
        lineService.deleteSection(1L, 1L);

        // then
        Line line = lineService.findLineById(1L);
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("정자역", "양재역");
    }
}
