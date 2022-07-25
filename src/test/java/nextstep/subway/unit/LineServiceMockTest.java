package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("삼성역");
        downStation = new Station("강남역");
        line = new Line("분당선", "bg-yellow-600");
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        given(stationService.findById(upStation.getId())).willReturn(upStation);
        given(stationService.findById(downStation.getId())).willReturn(downStation);
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 10);

        // when
        lineService.addSection(line.getId(), sectionRequest);

        // then
        verify(lineRepository).findById(line.getId());
        assertThat(line.getSections()).hasSize(1);
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection(){
        //given
        line.addSection(new Section(line, upStation, downStation, 10));
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));
        given(stationService.findById(anyLong())).willReturn(downStation);

        //when
        lineService.deleteSection(1L, 2L);

        //then
        assertThat(line.getSections()).isEmpty();
    }
}
