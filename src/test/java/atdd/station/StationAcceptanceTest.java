package atdd.station;

import atdd.AcceptanceTestSupport;
import atdd.line.LineHttpTestSupport;
import atdd.line.dto.LineCreateRequestDto;
import atdd.station.controller.StationController;
import atdd.station.dto.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AcceptanceTestSupport {

    private final String stationName = "강남역";

    private StationHttpTestSupport stationHttpTestSupport;
    private LineHttpTestSupport lineHttpTestSupport;

    @BeforeEach
    void setup() {
        this.stationHttpTestSupport = new StationHttpTestSupport(webTestClient);
        this.lineHttpTestSupport = new LineHttpTestSupport(webTestClient);
    }

    @DisplayName("지하철역 등록")
    @Test
    void create() {
        final String uri = StationController.ROOT_URI;
        final StationCreateRequestDto requestDto = StationCreateRequestDto.of(stationName);

        final EntityExchangeResult<StationResponseDto> result = webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), StationCreateRequestDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(StationResponseDto.class)
                .returnResult();


        final String location = result.getResponseHeaders().getLocation().getPath();
        final StationResponseDto responseView = result.getResponseBody();
        assertThat(location).isEqualTo(uri + "/" + responseView.getId());
        assertThat(responseView.getName()).isEqualTo(stationName);
    }

    @DisplayName("지하철역 목록 조회")
    @Test
    void findAll() {
        stationHttpTestSupport.createStation(StationCreateRequestDto.of(stationName));


        final List<StationResponseDto> results = webTestClient.get()
                .uri(StationController.ROOT_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StationResponseDto.class)
                .returnResult()
                .getResponseBody();


        final boolean contains = results.stream().anyMatch(response -> stationName.equals(response.getName()));
        assertThat(contains).isTrue();
    }

    @DisplayName("지하철역 정보 조회")
    @Test
    void getStation() {
        final StationResponseDto responseDto = stationHttpTestSupport.createStation(StationCreateRequestDto.of(stationName));


        final StationResponseDto stationResponseDto = webTestClient.get()
                .uri(StationController.ROOT_URI + "/" + responseDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StationResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(stationResponseDto).isNotNull();
        assertThat(stationResponseDto.getName()).isEqualTo(responseDto.getName());
    }

    @DisplayName("지하철역 삭제")
    @Test
    void delete() {
        final StationResponseDto responseDto = stationHttpTestSupport.createStation(StationCreateRequestDto.of(stationName));

        webTestClient.delete()
                .uri(StationController.ROOT_URI + "/" + responseDto.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @DisplayName("최단 거리 경로 조회")
    @Test
    void getShortestPath() throws Exception {
        final LocalTime startTime = LocalTime.MIN;
        final LocalTime endTime = LocalTime.MAX;
        final Long lineId2 = lineHttpTestSupport.createLineAndGetId(LineCreateRequestDto.of("2호선", startTime, endTime, 5));
        final Long lineId3 = lineHttpTestSupport.createLineAndGetId(LineCreateRequestDto.of("3호선", startTime, endTime, 5));

        final Long samsung = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("삼성역"));
        final Long seonreung = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("선릉역"));
        final Long yeoksam = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("역삼역"));
        final Long gangnam = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("강남역"));
        final Long gyodae = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("교대역"));
        final Long terminal = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("고속터미널역"));

        lineHttpTestSupport.addStations(lineId2, samsung, seonreung, yeoksam, gangnam, gyodae);
        lineHttpTestSupport.addStations(lineId3, gyodae, terminal);

        lineHttpTestSupport.addSection(lineId2, samsung, ofSectionRequest(seonreung));
        lineHttpTestSupport.addSection(lineId2, seonreung, ofSectionRequest(yeoksam));
        lineHttpTestSupport.addSection(lineId2, yeoksam, ofSectionRequest(gangnam));
        lineHttpTestSupport.addSection(lineId2, gangnam, ofSectionRequest(gyodae));
        lineHttpTestSupport.addSection(lineId3, terminal, ofSectionRequest(gyodae));

        final List<PathStation> expectedStations = Lists.list(
            PathStation.of(terminal, "고속터미널역"),
            PathStation.of(gyodae, "교대역"),
            PathStation.of(gangnam, "강남역"),
            PathStation.of(yeoksam, "역삼역"),
            PathStation.of(seonreung, "선릉역"),
            PathStation.of(samsung, "삼성역")
        );


        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startStationId", String.valueOf(terminal));
        params.add("endStationId", String.valueOf(samsung));

        final String requestUri = stationHttpTestSupport.makeRequestUri(StationController.ROOT_URI + "/shortest-path", params);
        final PathResponseDto result = webTestClient.get()
                .uri(requestUri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PathResponseDto.class)
                .returnResult()
                .getResponseBody();


        assertThat(result.getStartStationId()).isEqualTo(terminal);
        assertThat(result.getEndStationId()).isEqualTo(samsung);
        assertThat(result.getStations()).isEqualTo(expectedStations);
    }

    @DisplayName("최단 거리 경로 조회 - 동일 경로 재요청시 NotModified(304) 코드가 반환된다.")
    @Test
    void getShortestPathTwice() throws Exception {
        final LocalTime startTime = LocalTime.MIN;
        final LocalTime endTime = LocalTime.MAX;
        final Long lineId2 = lineHttpTestSupport.createLineAndGetId(LineCreateRequestDto.of("2호선", startTime, endTime, 5));

        final Long samsung = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("삼성역"));
        final Long seonreung = stationHttpTestSupport.createStationAndGetId(StationCreateRequestDto.of("선릉역"));

        lineHttpTestSupport.addStations(lineId2, samsung, seonreung);
        lineHttpTestSupport.addSection(lineId2, samsung, ofSectionRequest(seonreung));

        final List<PathStation> expectedStations = Lists.list(
                PathStation.of(samsung, "삼성역"),
                PathStation.of(seonreung, "선릉역")
        );

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startStationId", String.valueOf(samsung));
        params.add("endStationId", String.valueOf(seonreung));


        final EntityExchangeResult<PathResponseDto> entityResult = stationHttpTestSupport.findPath(samsung, seonreung);
        final String eTag = entityResult.getResponseHeaders().getETag();


        final String requestUri = stationHttpTestSupport.makeRequestUri(StationController.ROOT_URI + "/shortest-path", params);
        final EntityExchangeResult<PathResponseDto> result = webTestClient.get()
                .uri(requestUri)
                .header(HttpHeaders.IF_NONE_MATCH, eTag)
                .exchange()
                .expectBody(PathResponseDto.class)
                .returnResult();


        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_MODIFIED);

        final HttpHeaders cachedResponseHeaders = result.getResponseHeaders();
        assertThat(cachedResponseHeaders.getETag()).isEqualTo(eTag);

        assertThat(result.getResponseBody()).isNull();
    }

    private SectionCreateRequestDto ofSectionRequest(Long stationId) {
        return SectionCreateRequestDto.of(stationId, LocalTime.of(0, 5), 0.5);
    }

}
