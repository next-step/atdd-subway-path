package nextstep.subway.unit;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private static final String DONONGSTATIONNAME = "도농역";
    private static final String GOORISTATIONNAME = "구리역";
    private static final String DUCKSOSTATIONNAME = "덕소역";

    private static final String FIRSTLINENAME = "1호선";
    private static final String SECONDLINENAME = "2호선";

    private static final String BLUE = "blue";
    private static final String GREEN = "green";

    private Station donongStation;
    private Station gooriStation;
    private Station ducksoStation;

    private Line line;

    private Section firstSection;
    private Section secondSection;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @BeforeEach
    void setup() {
        donongStation = new Station(DONONGSTATIONNAME);
        gooriStation = new Station(GOORISTATIONNAME);
        ducksoStation = new Station(DUCKSOSTATIONNAME);

        line = new Line(FIRSTLINENAME, BLUE);

        firstSection = new Section(line, donongStation, gooriStation, 10);
        secondSection = new Section(line, gooriStation, ducksoStation, 5);
    }

    @Test
    void 라인_구간_안_만들고_저장() {
        // given
        when(lineRepository.save(any())).thenReturn(line);

        // when
        LineRequest request = new LineRequest(FIRSTLINENAME, BLUE, null, null, 10);
        LineResponse result = lineService.saveLine(request);

        //then
        assertAll(
            () -> assertEquals(FIRSTLINENAME, result.getName()),
            () -> assertEquals(BLUE, request.getColor()),
            () -> assertThat(result.getStations()).isEmpty()
        );
    }

    @Test
    void 라인_구간_만들고_저장() {
        // given
        when(stationService.findById(1L)).thenReturn(donongStation);
        when(stationService.findById(2L)).thenReturn(gooriStation);

        when(lineRepository.save(any())).thenReturn(line);

        // when
        LineRequest request = new LineRequest(FIRSTLINENAME, BLUE, 1L, 2L, 10);
        LineResponse result = lineService.saveLine(request);

        List<String> names = getNames(result);

        //then
        assertAll(
            () -> assertEquals(FIRSTLINENAME, result.getName()),
            () -> assertEquals(BLUE, request.getColor()),
            () -> assertThat(names).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );
    }

    @Test
    void 전체_라인_조회() {
        // given
        line.addSection(firstSection);

        Line secondLine = new Line(SECONDLINENAME, GREEN);
        secondLine.addSection(secondSection);

        when(lineRepository.findAll()).thenReturn(List.of(line, secondLine));

        // when
        List<LineResponse> result = lineService.showLines();

        assertThat(result).hasSize(2);

        // then
        LineResponse firstLineResponse = 모든_라인들에서_하나의_라인_꺼내기(result, FIRSTLINENAME);
        final List<String> stationNames = getNames(firstLineResponse);

        assertAll(
            () -> assertEquals(FIRSTLINENAME, firstLineResponse.getName()),
            () -> assertEquals(BLUE, firstLineResponse.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );

        LineResponse secondLineResponse = 모든_라인들에서_하나의_라인_꺼내기(result, SECONDLINENAME);
        final List<String> secondStationNames = getNames(secondLineResponse);

        assertAll(
            () -> assertEquals(SECONDLINENAME, secondLineResponse.getName()),
            () -> assertEquals(GREEN, secondLineResponse.getColor()),
            () -> assertThat(secondStationNames).contains(GOORISTATIONNAME, DUCKSOSTATIONNAME)
        );
    }

    private LineResponse 모든_라인들에서_하나의_라인_꺼내기(List<LineResponse> lineResponses, String name) {
        return lineResponses.stream()
            .filter(lineResponse -> lineResponse.getName().equals(name))
            .findFirst()
            .get();
    }

    @Test
    void 라인_단건_조회() {
        // given
        line.addSection(firstSection);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        LineResponse result = lineService.findById(1L);

        // then
        List<String> stationNames = getNames(result);

        assertAll(
            () -> assertEquals(FIRSTLINENAME, result.getName()),
            () -> assertEquals(BLUE, result.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );
    }

    private List<String> getNames(LineResponse lineResponse) {
        return lineResponse.getStations().stream()
            .map(StationResponse::getName)
            .collect(toList());
    }

    @Test
    void 라인_이름_변경() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        LineRequest request = LineRequest.builder()
            .name(SECONDLINENAME)
            .build();

        lineService.updateLine(1L, request);
        LineResponse result = lineService.findById(1L);

        // then
        assertEquals(SECONDLINENAME, result.getName());
    }

    @Test
    void 라인_컬러_변경() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        LineRequest request = LineRequest.builder()
            .color(GREEN)
            .build();

        lineService.updateLine(1L, request);
        LineResponse result = lineService.findById(1L);

        // then
        assertEquals(GREEN, result.getColor());
    }

    @Test
    void 라인_이름_컬러_변경() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        LineRequest request = LineRequest.builder()
            .name(SECONDLINENAME)
            .color(GREEN)
            .build();

        lineService.updateLine(1L, request);
        LineResponse result = lineService.findById(1L);

        // then
        assertAll(
            () -> assertEquals(SECONDLINENAME, result.getName()),
            () -> assertEquals(GREEN, result.getColor())
        );
    }

    @Test
    void 라인_삭제() {
        // when
        lineService.deleteLine(1L);
        verify(lineRepository, times(1)).deleteById(1L);

        // then
        List<LineResponse> result = lineService.showLines();

        assertThat(result).isEmpty();
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(1L)).thenReturn(donongStation);
        when(stationService.findById(2L)).thenReturn(gooriStation);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        lineService.addSection(1L, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse result = lineService.findById(1L);
        assertThat(result.getStations()).hasSize(2);
    }

    @Test
    void 라인_구간_삭제() {
        // given
        when(stationService.findById(1L)).thenReturn(donongStation);
        when(stationService.findById(2L)).thenReturn(gooriStation);
        when(stationService.findById(3L)).thenReturn(ducksoStation);

        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // when
        lineService.deleteSection(1L, 3L);

        // then
        LineResponse result = lineService.findById(1L);

        assertAll(
            () -> assertThat(result.getStations()).hasSize(2),
            () -> assertThat(result.getStations().get(0).getName()).isEqualTo(DONONGSTATIONNAME),
            () -> assertThat(result.getStations().get(1).getName()).isEqualTo(GOORISTATIONNAME)
        );
    }
}