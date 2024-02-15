package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {
    private static String name = "2호선";
    private static String color = "green";
    private static Long distance = 10L;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void saveLine() {
        // given
        LineResponse lineResponse = 라인_생성();

        // then
        Long responseId = lineResponse.getId();
        assertTrue(lineRepository.findById(responseId).isPresent());
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }

    @Test
    void findLineById() {
        // given
        LineResponse response = 라인_생성();

        // when
        LineResponse lineResponseById = lineService.findLineResponseById(response.getId());

        // then
        assertThat(lineResponseById.getName()).isEqualTo(name);
        assertThat(lineResponseById.getColor()).isEqualTo(color);
    }

    @Test
    void updateLine() {
        //given
        LineResponse response = 라인_생성();

        String updateName = "1호선";
        String updateColor = "blue";
        LineRequest lineRequest = new LineRequest(updateName, updateColor, null, null, null);

        // when
        lineService.updateLine(lineRequest, response.getId());

        // then
        Line updatedLine = lineService.findLineById(response.getId());

        assertThat(updatedLine.getName()).isEqualTo(updateName);
        assertThat(updatedLine.getColor()).isEqualTo(updateColor);
        assertNotNull(updatedLine.getDownStationId());
        assertNotNull(updatedLine.getUpStationId());
    }

    @Test
    void deleteLine() {
        //given
        LineResponse response = 라인_생성();

        //when
        lineService.deleteLine(response.getId());

        //then
        assertThrows(IllegalArgumentException.class, () -> lineService.findLineById(response.getId()));
        assertTrue(lineRepository.findById(response.getId()).isEmpty());
    }

    private LineResponse 라인_생성() {
        List<Station> stations = stationRepository.saveAll(List.of(
            new Station("강남역"),
            new Station("서초역"))
        );
        Station 강남역 = stations.get(0);
        Station 서초역 = stations.get(1);

        LineRequest lineRequest = new LineRequest(name, color, 강남역.getId(), 서초역.getId(), distance);
        return lineService.saveLine(lineRequest);
    }

}
