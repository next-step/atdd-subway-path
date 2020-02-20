package atdd.line;

import atdd.AcceptanceTestSupport;
import atdd.line.controller.LineController;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import atdd.station.StationHttpTestSupport;
import atdd.station.dto.SectionCreateRequestDto;
import atdd.station.dto.StationCreateRequestDto;
import atdd.station.dto.StationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTestSupport {

    private final LocalTime startTime = LocalTime.of(5, 0);
    private final LocalTime endTime = LocalTime.of(23, 50);
    private final int intervalTime = 0;

    private final String name1 = "2호선";
    private final String name2 = "3호선";

    private LineHttpTestSupport lineHttpTestSupport;
    private StationHttpTestSupport stationHttpTestSupport;

    @BeforeEach
    void setup() {
        this.lineHttpTestSupport = new LineHttpTestSupport(webTestClient);
        this.stationHttpTestSupport = new StationHttpTestSupport(webTestClient);
    }

    @DisplayName("지하철 노선 등록")
    @Test
    public void create() {
        final LineCreateRequestDto requestDto = new LineCreateRequestDto(name1, startTime, endTime, intervalTime);


        final EntityExchangeResult<LineResponseDto> result = webTestClient.post()
                .uri(LineController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), LineCreateRequestDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(LineResponseDto.class)
                .returnResult();


        final LineResponseDto responseDto = result.getResponseBody();
        final String location = result.getResponseHeaders().getLocation().getPath();
        assertThat(location).isEqualTo(LineController.ROOT_URI + "/" + responseDto.getId());
        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
        assertThat(responseDto.getStartTime()).isEqualTo(requestDto.getStartTime());
        assertThat(responseDto.getEndTime()).isEqualTo(requestDto.getEndTime());
        assertThat(responseDto.getIntervalTime()).isEqualTo(requestDto.getIntervalTime());
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void findAll() {
        final LineCreateRequestDto requestDto1 = new LineCreateRequestDto(name1, startTime, endTime, intervalTime);
        final LineCreateRequestDto requestDto2 = new LineCreateRequestDto(name2, startTime, endTime, intervalTime);
        lineHttpTestSupport.createLine(requestDto1);
        lineHttpTestSupport.createLine(requestDto2);

        final EntityExchangeResult<List<LineResponseDto>> result = webTestClient.get().uri(
                LineController.ROOT_URI)
                .exchange()
                .expectBodyList(LineResponseDto.class)
                .returnResult();


        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        final List<LineResponseDto> lineResponseDtos = result.getResponseBody();
        assertThat(lineResponseDtos).hasSize(2);
        assertEqual(lineResponseDtos.get(0), requestDto1);
        assertEqual(lineResponseDtos.get(1), requestDto2);
    }

    @DisplayName("지하철 노선 정보 조회")
    @Test
    void findAllByName() {
        final LineCreateRequestDto requestDto1 = new LineCreateRequestDto(name1, startTime, endTime, intervalTime);
        final LineCreateRequestDto requestDto2 = new LineCreateRequestDto(name2, startTime, endTime, intervalTime);
        lineHttpTestSupport.createLine(requestDto1);
        lineHttpTestSupport.createLine(requestDto2);


        final String requestURI = UriComponentsBuilder.fromUriString(LineController.ROOT_URI)
                .queryParam("name", name1)
                .build()
                .toUriString();

        final EntityExchangeResult<List<LineResponseDto>> result = webTestClient.get()
                .uri(requestURI)
                .acceptCharset(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBodyList(LineResponseDto.class)
                .returnResult();


        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        final List<LineResponseDto> lineResponseDtos = result.getResponseBody();
        assertThat(lineResponseDtos).hasSize(1);
        assertThat(lineResponseDtos.get(0).getName()).isEqualTo(name1);
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void delete() {
        final LineCreateRequestDto requestDto1 = new LineCreateRequestDto(name1, startTime, endTime, intervalTime);
        final LineCreateRequestDto requestDto2 = new LineCreateRequestDto(name2, startTime, endTime, intervalTime);

        final LineResponseDto createdResponse1 = lineHttpTestSupport.createLine(requestDto1);
        lineHttpTestSupport.createLine(requestDto2);


        final String requestURI = UriComponentsBuilder.fromUriString(LineController.ROOT_URI + "/{lineId}")
                .build(createdResponse1.getId())
                .toString();

        webTestClient.delete()
                .uri(requestURI)
                .acceptCharset(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();


        final List<LineResponseDto> lineResponseDtos = lineHttpTestSupport.findAll();

        assertThat(lineResponseDtos).hasSize(1);
        assertThat(lineResponseDtos.get(0).getName()).isNotEqualTo(createdResponse1.getName());
    }

    @DisplayName("지하철 노선에 역을 추가")
    @Test
    void addStation() throws Exception {
        final LineCreateRequestDto lineRequestDto = new LineCreateRequestDto(name1, startTime, endTime, intervalTime);
        final LineResponseDto createdLine = lineHttpTestSupport.createLine(lineRequestDto);

        final String stationName = "강남역";
        final StationCreateRequestDto stationRequestDto = StationCreateRequestDto.of(stationName);
        final StationResponseDto createdStation = stationHttpTestSupport.createStation(stationRequestDto);


        final String uri = LineController.ROOT_URI + "/{lineId}/stations/{stationId}";
        final Long lineId = createdLine.getId();
        final Long stationId = createdStation.getId();
        webTestClient.put()
                .uri(uri, lineId, stationId)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();


        final LineResponseDto responseDto = lineHttpTestSupport.findAll().get(0);
        assertThat(responseDto.getStations()).isEmpty();

        final StationResponseDto stationResponseDto = stationHttpTestSupport.getStation(stationId);

        final boolean existLine = stationResponseDto.getLines().stream()
                .anyMatch(lineDto -> Objects.equals(lineId, lineDto.getId()));

        assertThat(existLine).isTrue();
    }

    @DisplayName("지하철노선에 지하철 구간을 등록")
    @Test
    void addSection() {
        final LineCreateRequestDto lineRequestDto = new LineCreateRequestDto(name1, startTime, endTime, intervalTime);
        final LineResponseDto createdLine = lineHttpTestSupport.createLine(lineRequestDto);

        final StationResponseDto createdStation1 = stationHttpTestSupport.createStation(StationCreateRequestDto.of("강남역"));
        final StationResponseDto createdStation2 = stationHttpTestSupport.createStation(StationCreateRequestDto.of("역삼역"));

        lineHttpTestSupport.addStation(createdLine.getId(), createdStation1.getId());
        lineHttpTestSupport.addStation(createdLine.getId(), createdStation2.getId());


        final LocalTime duration = LocalTime.MAX;
        final double distance = 1.5;
        final SectionCreateRequestDto createRequestDto = SectionCreateRequestDto.of(createdStation2.getId(), duration, distance);
        final String uri = LineController.ROOT_URI + "/{lineId}/stations/{stationId}/sections";

        webTestClient.put()
                .uri(uri, createdLine.getId(), createdStation1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createRequestDto), SectionCreateRequestDto.class)
                .exchange()
                .expectStatus().isOk();


        final List<LineResponseDto> lineResponseDtos = lineHttpTestSupport.findAll();
        assertThat(lineResponseDtos).hasSize(1);

        final LineResponseDto lineResponseDto = lineResponseDtos.get(0);
        assertThat(lineResponseDto.getId()).isEqualTo(createdLine.getId());

        final List<LineResponseDto.StationDto> stations = lineResponseDto.getStations();
        assertThat(stations).hasSize(2);

        assertThat(stations.get(0).getId()).isEqualTo(createdStation1.getId());
        assertThat(stations.get(1).getId()).isEqualTo(createdStation2.getId());
    }

    @DisplayName("지하철노선에 지하철 구간을 제외")
    @Test
    void deleteSection() throws Exception {
        final LocalTime duration = LocalTime.of(2, 5);
        final double distance = 1.5;

        final LineResponseDto createdLine = lineHttpTestSupport.createLine(new LineCreateRequestDto(name1, startTime, endTime, intervalTime));
        final Long lineId = createdLine.getId();

        final StationResponseDto createdStation1 = stationHttpTestSupport.createStation(StationCreateRequestDto.of("강남역"));
        final StationResponseDto createdStation2 = stationHttpTestSupport.createStation(StationCreateRequestDto.of("역삼역"));
        final StationResponseDto createdStation3 = stationHttpTestSupport.createStation(StationCreateRequestDto.of("선릉역"));
        final Long stationId1 = createdStation1.getId();
        final Long stationId2 = createdStation2.getId();
        final Long stationId3 = createdStation3.getId();

        lineHttpTestSupport.addStation(lineId, stationId1);
        lineHttpTestSupport.addStation(lineId, stationId2);
        lineHttpTestSupport.addStation(lineId, stationId3);

        lineHttpTestSupport.addSection(lineId, stationId1, SectionCreateRequestDto.of(stationId2, duration, distance));
        lineHttpTestSupport.addSection(lineId, stationId2, SectionCreateRequestDto.of(stationId3, duration, distance));


        final String uri = LineController.ROOT_URI + "/{lineId}/stations/{stationId}";
        final String requestURI = UriComponentsBuilder.fromUriString(uri)
                .build(lineId, stationId2)
                .toString();

        webTestClient.delete()
                .uri(requestURI)
                .acceptCharset(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();


        final LineResponseDto line = lineHttpTestSupport.getLine(lineId);
        final List<LineResponseDto.StationDto> stationDtos = line.getStations();

        assertThat(stationDtos).hasSize(2);
        assertThat(stationDtos.get(0).getId()).isEqualTo(stationId1);
        assertThat(stationDtos.get(1).getId()).isEqualTo(stationId3);
    }

    private void assertEqual(LineResponseDto responseDto, LineCreateRequestDto requestDto) {
        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
        assertThat(responseDto.getStartTime()).isEqualTo(requestDto.getStartTime());
        assertThat(responseDto.getEndTime()).isEqualTo(requestDto.getEndTime());
        assertThat(responseDto.getIntervalTime()).isEqualTo(requestDto.getIntervalTime());
    }

}
