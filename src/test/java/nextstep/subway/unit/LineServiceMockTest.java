package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        BDDMockito.given(stationService.findById(1L))
                .willReturn(new Station("강남역"));
        BDDMockito.given(stationService.findById(2L))
                .willReturn(new Station("양재역"));
        BDDMockito.given(lineRepository.findById(any()))
                .willReturn(Optional.of(new Line("신분당선", "bg-red-500")));

        LineService lineService = new LineService(lineRepository, stationService);
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, sectionRequest);


        // then
        Line line = lineRepository.findById(1L).orElseThrow();
        SoftAssertions.assertSoftly(sa -> {
            sa.assertThat(line.getSections().size()).isEqualTo(1);
            sa.assertThat(line.getStations().size()).isEqualTo(2);
            sa.assertThat(line.getStations().stream().map(Station::getName)).containsOnly("강남역", "양재역");
        });
    }
}
