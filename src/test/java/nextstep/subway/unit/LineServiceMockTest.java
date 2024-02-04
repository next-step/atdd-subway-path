package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");
        Line 그린선 = new Line(1L, "2호선", "green");

        given(lineRepository.findById(any()))
                .willReturn(Optional.of(그린선));
        given(stationService.findById(eq(1L)))
                .willReturn(강남역);
        given(stationService.findById(eq(2L)))
                .willReturn(선릉역);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L,
                new SectionRequest(
                        1L, 2L, 10
                )
        );

        // then
        // lineService.findById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(1L);
        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(1L),
                () -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("green")
        );

    }
}
