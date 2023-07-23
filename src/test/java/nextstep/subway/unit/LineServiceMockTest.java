package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;
    private Station station_1;
    private Station station_2;
    private Station station_3;

    static Long LINE_ID_1 = 1L;
    static String LINE_NAME_1 = "분당선";
    static String LINE_COLOR_1 = "Yellow";
    static String LINE_NAME_UPDATE = "신분당선";
    static String LINE_COLOR_UPDATE = "RED";
    static Long LINE_ID_1000 = 1000L;
    static Long STATION_ID_1 = 1L;
    static String STATION_NAME_1 = "강남역";
    static Long STATION_ID_2 = 2L;
    static String STATION_NAME_2 = "역삼역";
    static Long STATION_ID_3 = 3L;
    static int DISTANCE_10 = 10;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
        station_1 = new Station(STATION_NAME_1);
        station_2 = new Station(STATION_NAME_2);
    }

    @Test
    @DisplayName("새로운 노선을 저장")
    void saveLine() {
        // given
        LineRequest request = LineRequest.builder().name(LINE_NAME_1).color(LINE_COLOR_1)
                .upStationId(STATION_ID_1).downStationId(STATION_ID_2).distance(DISTANCE_10).build();
        Line line = new Line(request.getName(), request.getColor());
        given(lineRepository.save(any())).willReturn(line);
        given(stationService.findById(STATION_ID_1)).willReturn(station_1);
        given(stationService.findById(STATION_ID_2)).willReturn(station_2);

        // when
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getName()).isNotNull();
        assertThat(response.getName()).isEqualTo(LINE_NAME_1);
    }

    @Test
    @DisplayName("기존 노선을 수정")
    void updateLine() {
        // given
        LineRequest request = LineRequest.builder().name(LINE_NAME_UPDATE).color(LINE_COLOR_UPDATE).build();
        Line line = new Line(LINE_NAME_1, LINE_COLOR_1);
        given(lineRepository.findById(any())).willReturn(
                Optional.of(line));

        // when
        lineService.updateLine(LINE_ID_1000, request);

        // then
        assertThat(line.getName()).isEqualTo(LINE_NAME_UPDATE);
        assertThat(line.getColor()).isEqualTo(LINE_COLOR_UPDATE);
    }

    @Test
    @DisplayName("기존 노선을 삭제")
    void deleteLine() {
        // when
        lineService.deleteLine(LINE_ID_1000);

        // then
        verify(lineRepository).deleteById(LINE_ID_1000);
    }


    @Test
    @DisplayName("기존 노선에 구간을 추가")
    void addSection() {
        // given
        SectionRequest request = new SectionRequest(STATION_ID_2, STATION_ID_3, DISTANCE_10);
        Line line = new Line(LINE_NAME_1, LINE_COLOR_1);
        given(stationService.findById(STATION_ID_2)).willReturn(station_2);
        given(stationService.findById(STATION_ID_3)).willReturn(station_3);
        given(lineRepository.findById(LINE_ID_1000)).willReturn(Optional.of(line));

        // when
        lineService.addSection(LINE_ID_1000, request);

        // then
        assertThat(line).isNotNull();
        Section lastSection = line.getSections().get(line.getSections().size() - 1);
        assertThat(lastSection.getUpStation()).isEqualTo(station_2);
        assertThat(lastSection.getDownStation()).isEqualTo(station_3);
        assertThat(lastSection.getDistance()).isEqualTo(DISTANCE_10);
    }
}
