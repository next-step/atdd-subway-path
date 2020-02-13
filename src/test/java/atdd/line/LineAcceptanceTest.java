package atdd.line;

import atdd.line.domain.Line;
import atdd.line.dto.CreateEdgeRequest;
import atdd.line.dto.CreateEdgesAndStationsRequest;
import atdd.line.dto.CreateLineAndStationsRequest;
import atdd.line.dto.CreateLineRequest;
import atdd.station.dto.CreateStationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리")
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
	private static final Logger logger = LoggerFactory.getLogger(LineAcceptanceTest.class);

	@Autowired
	private WebTestClient webTestClient;

	@DisplayName("지하철 노선 등록")
	@Test
	void createLine() {
		// given
		CreateLineRequest request = CreateLineRequest.builder()
			.name("2호선")
			.startTime("05:00")
			.endTime("23:50")
			.intervalTime("10")
			.build();

		// when (관리자는 "2호선" 지하철 노선 등록을 요청한다.)
		// then ("2호선" 지하철 노선이 등록 되었다.)
		webTestClient.post().uri("/lines")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(request), CreateLineAndStationsRequest.class)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectHeader().exists("Location")
			.expectBody()
			.jsonPath("$.name").isEqualTo("2호선")
			.jsonPath("$.startTime").isEqualTo("05:00")
			.jsonPath("$.endTime").isEqualTo("23:50")
			.jsonPath("$.intervalTime").isEqualTo("10");
	}

	@DisplayName("지하철 노선 목록 조회")
	@Test
	void findAllLines() {
		// given ("2호선" 지하철 노선이 등록되어 있다.)
		createLineStub();

		// when (사용자는 지하철 노선의 목록 조회를 요청한다.)
		EntityExchangeResult<List<Line>> result = webTestClient.get().uri("/lines")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Line.class)
			.returnResult();

		// then (지하철 노선의 목록을 응답받는다.)
		assertThat(result.getResponseBody()).flatExtracting(Line::getName)
			.contains("2호선");
	}

	@DisplayName("지하철 노선 정보 조회")
	@Test
	void findLine() {
		// given ("2호선" 지하철 노선이 등록되어 있다.)
		Line line = CreateLineRequest.builder()
			.name("2호선")
			.startTime("05:00")
			.endTime("23:50")
			.intervalTime("10")
			.build().toEntity();
		CreateLineAndStationsRequest createLineAndStationsRequest = CreateLineAndStationsRequest.builder()
			.line(line)
			.edgesAndStations(createEdgesAndStationsRequests())
			.build();
		webTestClient.post().uri("/line-stations")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(createLineAndStationsRequest), CreateLineAndStationsRequest.class)
			.exchange();

		// when (사용자는 "2호선" 지하철 노선의 정보 조회를 요청한다.)
		// then ("2호선" 지하철 노선의 정보를 응답받는다.)
		webTestClient.get().uri(uriBuilder -> uriBuilder
			.path("/line")
			.queryParam("name", "2호선")
			.build())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.name").isEqualTo("2호선")
			.jsonPath("$.startTime").isEqualTo("05:00")
			.jsonPath("$.endTime").isEqualTo("23:50")
			.jsonPath("$.intervalTime").isEqualTo("10")
			.jsonPath("$.stations").isNotEmpty();
	}

	@DisplayName("지하철 노선 삭제")
	@Test
	void deleteLine() {
		// given ("2호선" 지하철 노선이 등록되어 있다.)

		// when (관리자는 "2호선" 지하철 노선 삭제를 요청한다.)

		// then ("2호선" 지하철 노선이 삭제되었다.)
	}

	private void createLineStub() {
		CreateLineRequest request = CreateLineRequest.builder()
			.name("2호선")
			.startTime("05:00")
			.endTime("23:50")
			.intervalTime("10")
			.build();
		webTestClient.post().uri("/lines")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(request), CreateLineAndStationsRequest.class)
			.exchange();
	}

	// TODO: 별도 클래스로 분리, static 변수화
	private List<CreateEdgesAndStationsRequest> createEdgesAndStationsRequests() {
		return List.of(
			CreateEdgesAndStationsRequest.builder()
				.edge(CreateEdgeRequest.builder()
					.elapsedTime(2.0)
					.distance(1.2)
					.build()
				)
				.sourceStation(CreateStationRequest.builder()
					.name("교대역")
					.build())
				.targetStation(CreateStationRequest.builder()
					.name("강남역")
					.build())
				.build(),
			CreateEdgesAndStationsRequest.builder()
				.edge(CreateEdgeRequest.builder()
					.elapsedTime(1.5)
					.distance(0.8)
					.build()
				)
				.sourceStation(CreateStationRequest.builder()
					.name("강남역")
					.build())
				.targetStation(CreateStationRequest.builder()
					.name("역삼역")
					.build())
				.build(),
			CreateEdgesAndStationsRequest.builder()
				.edge(CreateEdgeRequest.builder()
					.elapsedTime(2.0)
					.distance(1.2)
					.build()
				)
				.sourceStation(CreateStationRequest.builder()
					.name("역삼역")
					.build())
				.targetStation(CreateStationRequest.builder()
					.name("선릉역")
					.build())
				.build(),
			CreateEdgesAndStationsRequest.builder()
				.edge(CreateEdgeRequest.builder()
					.elapsedTime(2.0)
					.distance(1.3)
					.build()
				)
				.sourceStation(CreateStationRequest.builder()
					.name("선릉역")
					.build())
				.targetStation(CreateStationRequest.builder()
					.name("삼성역")
					.build())
				.build()
		);
	}
}
