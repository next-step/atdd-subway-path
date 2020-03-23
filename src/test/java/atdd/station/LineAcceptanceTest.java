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
    private StationTestHelper stationTestHelper;
    private LineTestHelper lineTestHelper;

    @BeforeEach
    void setUp() {
        this.stationTestHelper = new StationTestHelper(webTestClient);
        this.lineTestHelper = new LineTestHelper(webTestClient);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createLine() {
        //when
        LineResponseDto lineResponseDto = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_1);

        // then
        assertThat(lineResponseDto.getName()).isEqualTo(LINE_NAME_1);
        assertThat(lineResponseDto.getStations().size()).isEqualTo(0);
    }

    @Test
    public void findAllLines() {
        // given
        LineResponseDto lineResponseDto = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_1);

        // when
        List<LineResponseDto> lineResponseDtos = lineTestHelper.findAll();

        //then
        assertThat(lineResponseDtos.size()).isEqualTo(1);
        assertThat(lineResponseDtos.get(0).getName()).isEqualTo(LINE_NAME_1);
        assertThat(lineResponseDtos.get(0).getStations().size()).isEqualTo(0);
    }

    @Test
    public void findLine() {
        // given
        long lineId = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_1).getId();

        // when
        LineResponseDto lineResponseDto = lineTestHelper.findById(lineId);

        // then
        assertThat(lineResponseDto.getName()).isEqualTo(LINE_NAME_1);
    }

    @Test
    public void deleteLine() {
        // given
        long lineId = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_1).getId();

        // when
        lineTestHelper.deleteLine(lineId);

        // then
        List<LineResponseDto> lineResponseDtos = lineTestHelper.findAll();

        assertThat(lineResponseDtos.size()).isEqualTo(0);
    }

    @Test
    public void addEdge() {
        // given
        StationDto station1 = stationTestHelper.createStation(STATION_NAME);
        StationDto station2 = stationTestHelper.createStation(STATION_NAME_2);
        StationDto station3 = stationTestHelper.createStation(STATION_NAME_3);

        LineResponseDto lineResponseDto = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_1);

        // when
        LineResponseDto resultLine = lineTestHelper.addEdge(lineResponseDto.getId(), station1.getId(), station2.getId(), 10, 10);

        // then
        assertThat(resultLine.getStations().size()).isEqualTo(2);
        assertThat(resultLine.getStations().get(0).getName()).isEqualTo(STATION_NAME);
        assertThat(resultLine.getStations().get(1).getName()).isEqualTo(STATION_NAME_2);
    }

    @Test
    public void deleteEdge() {
        // given
        StationDto station1 = stationTestHelper.createStation(STATION_NAME);
        StationDto station2 = stationTestHelper.createStation(STATION_NAME_2);
        StationDto station3 = stationTestHelper.createStation(STATION_NAME_3);

        LineResponseDto lineResponseDto = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_1);

        lineTestHelper.addEdge(lineResponseDto.getId(), station1.getId(), station2.getId(), 10, 10);
        lineTestHelper.addEdge(lineResponseDto.getId(), station2.getId(), station3.getId(), 10, 10);

        // when
        lineTestHelper.deleteEdge(lineResponseDto.getId(), station2.getId());

        // then
        LineResponseDto resultLine = lineTestHelper.findById(lineResponseDto.getId());

        assertThat(resultLine.getStations().size()).isEqualTo(2);
        assertThat(resultLine.getStations().get(0).getName()).isEqualTo(STATION_NAME);
        assertThat(resultLine.getStations().get(1).getName()).isEqualTo(STATION_NAME_3);
    }
}
