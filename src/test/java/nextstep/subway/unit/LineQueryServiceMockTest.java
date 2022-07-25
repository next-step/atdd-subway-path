package nextstep.subway.unit;

import nextstep.subway.applicaion.LineQueryService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.GREEN;
import static nextstep.subway.utils.GivenUtils.TEN;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.강남역_이름;
import static nextstep.subway.utils.GivenUtils.신분당선;
import static nextstep.subway.utils.GivenUtils.신분당선_이름;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.양재역_이름;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.역삼역_이름;
import static nextstep.subway.utils.GivenUtils.이호선;
import static nextstep.subway.utils.GivenUtils.이호선_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LineQueryServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private LineQueryService lineQueryService;

    @Test
    @DisplayName("지하철 노선 목록 조회")
    void findAllLines() {
        // given
        int expectedSize = 2;
        Line 이호선 = 이호선();
        Line 신분당선 = 신분당선();
        이호선.addSection(강남역(), 역삼역(), TEN);
        신분당선.addSection(강남역(), 양재역(), FIVE);
        doReturn(List.of(이호선, 신분당선)).when(lineRepository)
                .findAll();

        // when
        List<LineResponse> allLines = lineQueryService.findAllLines();
        List<String> lineNames = getLineNames(allLines);
        List<List<String>> lineStationNames = getLineStationNames(allLines);

        // then
        assertThat(allLines).hasSize(expectedSize);
        assertThat(lineNames).containsExactly(이호선_이름, 신분당선_이름);
        assertThat(lineStationNames.get(0)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
        assertThat(lineStationNames.get(1)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 양재역_이름);
    }

    @Test
    @DisplayName("지하철 노선 조회 - lineResponse")
    void findLine() {
        // given
        int expectedSize = 2;
        Line 이호선 = 이호선();
        이호선.addSection(강남역(), 역삼역(), TEN);
        doReturn(Optional.ofNullable(이호선)).when(lineRepository)
                .findById(이호선.getId());

        // when
        LineResponse line = lineQueryService.findLine(이호선.getId());

        // then
        assertThat(line.getName()).isEqualTo(이호선_이름);
        assertThat(line.getColor()).isEqualTo(GREEN);
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("지하철 노선 조회 - line")
    void findById() {
        // given
        int expectedSize = 2;
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        이호선.addSection(강남역, 역삼역, TEN);
        doReturn(Optional.ofNullable(이호선)).when(lineRepository)
                .findById(이호선.getId());

        // when
        Line line = lineQueryService.findById(이호선.getId());

        // then
        assertThat(line.getName()).isEqualTo(이호선_이름);
        assertThat(line.getColor()).isEqualTo(GREEN);
        assertThat(line.getStations()).hasSize(expectedSize)
                .containsExactly(강남역, 역삼역);
    }

    private List<String> getLineNames(List<LineResponse> allLines) {
        return allLines.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
    }

    private List<List<String>> getLineStationNames(List<LineResponse> allLines) {
        return allLines.stream()
                .map(this::getStationNames)
                .collect(Collectors.toList());
    }

    private List<String> getStationNames(LineResponse lineResponse) {
        return lineResponse.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }

}
