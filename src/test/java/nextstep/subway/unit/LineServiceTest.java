package nextstep.subway.unit;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {

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

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setup() {
        donongStation = new Station(DONONGSTATIONNAME);
        gooriStation = new Station(GOORISTATIONNAME);
        ducksoStation = new Station(DUCKSOSTATIONNAME);

        line = new Line(FIRSTLINENAME, BLUE);

        firstSection = new Section(line, donongStation, gooriStation, 10);
        secondSection = new Section(line, gooriStation, ducksoStation, 5);

        stationRepository.save(donongStation);
        stationRepository.save(gooriStation);
        stationRepository.save(ducksoStation);
    }

    @Test
    void 라인_구간_안_만들고_저장() {
        // when
        LineResponse lineResponse = 라인_저장(FIRSTLINENAME, BLUE, null, null, 10);

        // then
        assertAll(
            () -> assertEquals(FIRSTLINENAME, lineResponse.getName()),
            () -> assertEquals(BLUE, lineResponse.getColor()),
            () -> assertThat(lineResponse.getStations()).isEmpty()
        );
    }

    @Test
    void 라인_구간_만들고_저장() {
        // when
        LineResponse lineResponse = 라인_저장(FIRSTLINENAME, BLUE, donongStation.getId(), gooriStation.getId(), 10);

        // then
        List<String> stationNames = 라인_역들에서_이름들_꺼내기(lineResponse);

        assertAll(
            () -> assertEquals(FIRSTLINENAME, lineResponse.getName()),
            () -> assertEquals(BLUE, lineResponse.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );
    }

    private LineResponse 라인_저장(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest request = new LineRequest(name, color, upStationId, downStationId, distance);
        return lineService.saveLine(request);
    }

    @Test
    void 전체_라인_조회() {
        // given
        line.addSection(firstSection);
        lineRepository.save(line);

        Line secondLine = new Line(SECONDLINENAME, GREEN);
        secondLine.addSection(secondSection);
        lineRepository.save(secondLine);

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        LineResponse firstLineResponse = 모든_라인들에서_하나의_라인_꺼내기(lineResponses, FIRSTLINENAME);
        final List<String> stationNames = 라인_역들에서_이름들_꺼내기(firstLineResponse);

        assertAll(
            () -> assertEquals(FIRSTLINENAME, firstLineResponse.getName()),
            () -> assertEquals(BLUE, firstLineResponse.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );

        LineResponse secondLineResponse = 모든_라인들에서_하나의_라인_꺼내기(lineResponses, SECONDLINENAME);
        final List<String> secondStationNames = 라인_역들에서_이름들_꺼내기(secondLineResponse);

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
        lineRepository.save(line);

        // when
        LineResponse result = lineService.findById(line.getId());

        // then
        List<String> stationNames = 라인_역들에서_이름들_꺼내기(result);

        assertAll(
            () -> assertEquals(FIRSTLINENAME, result.getName()),
            () -> assertEquals(BLUE, result.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );
    }

    @Test
    void 라인_이름_변경() {
        // given
        line.addSection(firstSection);
        lineRepository.save(line);

        //when
        LineRequest request = LineRequest.builder()
            .name(SECONDLINENAME)
            .build();

        lineService.updateLine(line.getId(), request);
        LineResponse result = lineService.findById(line.getId());

        // then
        List<String> stationNames = 라인_역들에서_이름들_꺼내기(result);

        assertAll(
            () -> assertEquals(SECONDLINENAME, result.getName()),
            () -> assertEquals(BLUE, result.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );
    }

    @Test
    void 라인_컬러_변경() {
        // given
        line.addSection(firstSection);
        lineRepository.save(line);

        //when
        LineRequest request = LineRequest.builder()
            .color(GREEN)
            .build();

        lineService.updateLine(line.getId(), request);
        LineResponse result = lineService.findById(line.getId());

        // then
        List<String> stationNames = 라인_역들에서_이름들_꺼내기(result);

        assertAll(
            () -> assertEquals(FIRSTLINENAME, result.getName()),
            () -> assertEquals(GREEN, result.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );
    }

    @Test
    void 라인_이름_컬러_변경() {
        // given
        line.addSection(firstSection);

        lineRepository.save(line);

        //when
        LineRequest request = LineRequest.builder()
            .name(SECONDLINENAME)
            .color(GREEN)
            .build();

        lineService.updateLine(line.getId(), request);
        LineResponse result = lineService.findById(line.getId());

        // then
        List<String> stationNames = 라인_역들에서_이름들_꺼내기(result);

        assertAll(
            () -> assertEquals(SECONDLINENAME, result.getName()),
            () -> assertEquals(GREEN, result.getColor()),
            () -> assertThat(stationNames).contains(DONONGSTATIONNAME, GOORISTATIONNAME)
        );
    }

    private List<String> 라인_역들에서_이름들_꺼내기(LineResponse lineResponse) {
        return lineResponse.getStations().stream()
            .map(StationResponse::getName)
            .collect(toList());
    }

    @Test
    void 라인_삭제() {
        // given
        line.addSection(firstSection);
        lineRepository.save(line);

        // when
        lineService.deleteLine(line.getId());

        // then
        List<LineResponse> result = lineService.showLines();

        assertThat(result).isEmpty();
    }

    @Test
    void 구간_추가() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        lineRepository.save(line);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(donongStation.getId(), gooriStation.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(
            () -> assertEquals(firstSection, line.getLastSection()),
            () -> assertThat(line.getSections().getSections()).contains(firstSection),
            () -> assertThat(line.getSections().getAllStation()).containsExactlyElementsOf(Arrays.asList(donongStation, gooriStation))
        );
    }

    @Test
    void 라인_구간_삭제() {
        // given
        line.addSection(firstSection);
        line.addSection(secondSection);

        lineRepository.save(line);

        // when
        lineService.deleteSection(line.getId(), ducksoStation.getId());

        // then
        assertThat(line.getSections().getAllStation()).containsExactlyElementsOf(Arrays.asList(donongStation, gooriStation));
    }
}