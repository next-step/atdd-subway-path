package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @InjectMocks
    private LineService lineService;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("8호선", "bg-pink-500")));
        given(stationService.findById(1L)).willReturn(new Station("암사역"));
        given(stationService.findById(2L)).willReturn(new Station("모란역"));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 17));

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(1L).getStations()).hasSize(2);
    }

    @Test
    void 노선을_저장한다() {
        // given
        LineRequest request = new LineRequest("8호선", "bg-pink-500", 1L, 2L, 17);
        given(lineRepository.save(any())).willReturn(new Line(request.getName(), request.getColor()));
        given(stationService.findById(1L)).willReturn(new Station("암사역"));
        given(stationService.findById(2L)).willReturn(new Station("모란역"));

        // when
        LineResponse response = lineService.saveLine(request);

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo("8호선");
            assertThat(response.getColor()).isEqualTo("bg-pink-500");
            assertThat(response.getStations()).hasSize(2);
        });
    }

    @Test
    void 노선목록을_조회한다() {
        // given
        given(lineRepository.findAll()).willReturn(
                List.of(new Line("8호선", "bg-pink-500"), new Line("2호선", "bg-lime-300"))
        );

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertAll(() -> {
            assertThat(lineResponses).hasSize(2);
            assertThat(lineResponses.stream().map(LineResponse::getName)).containsExactly("8호선", "2호선");
            assertThat(lineResponses.stream().map(LineResponse::getColor)).containsExactly("bg-pink-500", "bg-lime-300");
        });
    }

    @Test
    void 노선을_조회한다() {
        // given
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("8호선", "bg-pink-500")));

        // when
        LineResponse response = lineService.findById(1L);

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo("8호선");
            assertThat(response.getColor()).isEqualTo("bg-pink-500");
        });
    }

    @Test
    void 노선을_정보를_수정하라() {
        // given
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("8호선", "bg-pink-500")));

        // when
        lineService.updateLine(1L, new LineRequest("2호선", "bg-lime-300", 1L, 2L, 10));

        // then
        LineResponse response = lineService.findById(1L);
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo("2호선");
            assertThat(response.getColor()).isEqualTo("bg-lime-300");
        });
    }

    @Test
    void 노선을_삭제하라() {
        // when
        lineService.deleteLine(1L);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> lineService.findById(1L));
    }
}
