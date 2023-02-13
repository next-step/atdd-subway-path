package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CannotDeleteSoleSectionException;
import nextstep.subway.exception.InvalidSectionDistanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    private Line 분당선;
    private Station 수서역;
    private Station 복정역;
    private Station 가천대역;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);

        분당선 = new Line(1L, "분당선", "yellow");
        수서역 = new Station(1L, "수서역");
        복정역 = new Station(2L, "복정역");
        가천대역 = new Station(3L, "가천대역");
    }

    @DisplayName("새로운 지하철 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);

        // when
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 구간 등록 시, 상행역과 하행역이 같으면 예외가 발생한다.")
    @Test
    void identicalStations() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);

        SectionRequest request = new SectionRequest(수서역.getId(), 수서역.getId(), 5);

        // when & then
        assertThatThrownBy(() -> lineService.addSection(분당선.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 등록 시, 구간의 길이는 최소 1 이상이어야 한다.")
    @ValueSource(ints = {-1, 0})
    @ParameterizedTest
    void invalidDistance(int distance) {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);

        SectionRequest request = new SectionRequest(수서역.getId(), 복정역.getId(), distance);

        // when & then
        assertThatThrownBy(() -> lineService.addSection(분당선.getId(), request))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("첫 번째 지하철 구간을 제거한다.")
    @Test
    void deleteFirstSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);
        when(stationService.findById(가천대역.getId())).thenReturn(가천대역);

        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));
        lineService.addSection(분당선.getId(), new SectionRequest(복정역.getId(), 가천대역.getId(), 5));

        // when
        lineService.deleteSection(분당선.getId(), 수서역.getId());

        // then
        assertAll(
            () -> assertThat(분당선.getStations()).containsExactly(복정역, 가천대역),
            () -> assertThat(분당선.getSections().get(0).getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("마지막 지하철 구간을 제거한다.")
    @Test
    void deleteLastSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);
        when(stationService.findById(가천대역.getId())).thenReturn(가천대역);

        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));
        lineService.addSection(분당선.getId(), new SectionRequest(복정역.getId(), 가천대역.getId(), 5));

        // when
        lineService.deleteSection(분당선.getId(), 가천대역.getId());

        // then
        assertAll(
            () -> assertThat(분당선.getStations()).containsExactly(수서역, 복정역),
            () -> assertThat(분당선.getSections().get(0).getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("중간역을 포함하는 지하철 구간을 제거한다.")
    @Test
    void deleteIntermediateSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);
        when(stationService.findById(가천대역.getId())).thenReturn(가천대역);

        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));
        lineService.addSection(분당선.getId(), new SectionRequest(복정역.getId(), 가천대역.getId(), 5));

        // when
        lineService.deleteSection(분당선.getId(), 복정역.getId());

        // then
        assertAll(
            () -> assertThat(분당선.getStations()).containsExactly(수서역, 가천대역),
            () -> assertThat(분당선.getSections().get(0).getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("지하철 구간 제거 시, 노선에 등록된 구간이 하나라면 예외가 발생한다.")
    @Test
    void cannotDeleteSectionWhenSingleSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);

        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // when & then
        assertThatThrownBy(() -> lineService.deleteSection(분당선.getId(), 수서역.getId()))
            .isInstanceOf(CannotDeleteSoleSectionException.class);
    }
}
