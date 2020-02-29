package atdd.station;

import atdd.station.model.dto.LineResponseDto;
import atdd.station.model.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest {
    private StationTestUtils stationTestUtils;
    private LineTestUtils lineTestUtils;

    @BeforeEach
    void setUp() {
        this.stationTestUtils = new StationTestUtils(webTestClient);
        this.lineTestUtils = new LineTestUtils(webTestClient);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createLine() {
        //when
        LineResponseDto lineResponseDto = lineTestUtils.createLine(CREATE_LINE_REQUEST_VIEW_1);

        // then
        assertThat(lineResponseDto.getName()).isEqualTo(LINE_NAME_1);
        assertThat(lineResponseDto.getStations().size()).isEqualTo(0);
    }

    @Test
    public void findAllLines() {
        // given
        LineResponseDto lineResponseDto = lineTestUtils.createLine(CREATE_LINE_REQUEST_VIEW_1);

        // when
        List<LineResponseDto> lineResponseDtos = lineTestUtils.findAll();

        //then
        assertThat(lineResponseDtos.size()).isEqualTo(1);
        assertThat(lineResponseDtos.get(0).getName()).isEqualTo(LINE_NAME_1);
        assertThat(lineResponseDtos.get(0).getStations().size()).isEqualTo(0);
    }

    @Test
    public void findLine() {
        // given
        long lineId = lineTestUtils.createLine(CREATE_LINE_REQUEST_VIEW_1).getId();

        // when
        LineResponseDto lineResponseDto = lineTestUtils.findById(lineId);

        // then
        assertThat(lineResponseDto.getName()).isEqualTo(LINE_NAME_1);
    }

    @Test
    public void deleteLine() {
        // given
        long lineId = lineTestUtils.createLine(CREATE_LINE_REQUEST_VIEW_1).getId();

        // when
        lineTestUtils.deleteLine(lineId);

        // then
        List<LineResponseDto> lineResponseDtos = lineTestUtils.findAll();

        assertThat(lineResponseDtos.size()).isEqualTo(0);
    }

    @Test
    public void addEdge() {
        // given
        StationDto station1 = stationTestUtils.createStation(STATION_NAME);
        StationDto station2 = stationTestUtils.createStation(STATION_NAME_2);
        StationDto station3 = stationTestUtils.createStation(STATION_NAME_3);

        LineResponseDto lineResponseDto = lineTestUtils.createLine(CREATE_LINE_REQUEST_VIEW_1);

        // when
        LineResponseDto resultLine = lineTestUtils.addEdge(lineResponseDto.getId(), station1.getId(), station2.getId());

        // then
        assertThat(resultLine.getStations().size()).isEqualTo(2);
        assertThat(resultLine.getStations().get(0).getName()).isEqualTo(STATION_NAME);
        assertThat(resultLine.getStations().get(1).getName()).isEqualTo(STATION_NAME_2);
    }

    @Test
    public void deleteEdge() {
        // given
        StationDto station1 = stationTestUtils.createStation(STATION_NAME);
        StationDto station2 = stationTestUtils.createStation(STATION_NAME_2);
        StationDto station3 = stationTestUtils.createStation(STATION_NAME_3);

        LineResponseDto lineResponseDto = lineTestUtils.createLine(CREATE_LINE_REQUEST_VIEW_1);

        lineTestUtils.addEdge(lineResponseDto.getId(), station1.getId(), station2.getId());
        lineTestUtils.addEdge(lineResponseDto.getId(), station2.getId(), station3.getId());

        // when
        lineTestUtils.deleteEdge(lineResponseDto.getId(), station2.getId());

        // then
        LineResponseDto resultLine = lineTestUtils.findById(lineResponseDto.getId());

        assertThat(resultLine.getStations().size()).isEqualTo(2);
        assertThat(resultLine.getStations().get(0).getName()).isEqualTo(STATION_NAME);
        assertThat(resultLine.getStations().get(1).getName()).isEqualTo(STATION_NAME_3);
    }
}
