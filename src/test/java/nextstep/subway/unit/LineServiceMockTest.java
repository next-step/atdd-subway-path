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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private Station 선릉역;

    private Station 삼성역;

    private Station 종합운동장역;

    private Line _2호선;

    @BeforeEach
    void setUp() {
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        종합운동장역 = new Station("삼성역");
        _2호선 = new Line("2호선", "bg-green-600");

        when(stationService.findById(1L)).thenReturn(선릉역);
        when(stationService.findById(2L)).thenReturn(삼성역);

        when(lineRepository.findById(1L)).thenReturn(Optional.of(_2호선));
    }

    @DisplayName("구간을 추가할 수 있다.")
    @Test
    void addSection() {
        // given
        final var sectionRequest = new SectionRequest(1L, 2L, 10);

        // when
        lineService.addSection(1L, sectionRequest);

        // then
        assertThat(_2호선.findAllStations()).containsExactly(선릉역, 삼성역);
    }


    @DisplayName("하행 종착역 구간을 삭제할 수 있다.")
    @Test
    void deleteSection() {
        // given

        when(stationService.findById(3L)).thenReturn(종합운동장역);

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 5));

        // when
        lineService.deleteSection(1L, 3L);

        // then
        assertThat(_2호선.findAllStations()).containsExactly(선릉역, 삼성역);
    }

    @DisplayName("삭제하고자 하는 구간이 상행 종착역과 하행 종착역만이 있다면 삭제 시 에러가 발생한다.")
    @Test
    void deleteExceptionWhenOnlyOneSectionExist() {
        // given
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // when, then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역만 존재하기 때문에 삭제할 수 없습니다.");
    }

    @DisplayName("삭제하고자 하는 역이 하행 종착역이 아니면 에러가 발생한다.")
    @Test
    void deleteExceptionWhenStationIsNotDownStation() {
        // given

        when(stationService.findById(3L)).thenReturn(종합운동장역);

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 5));

        // when, then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역만 삭제할 수 있습니다.");

    }
}
