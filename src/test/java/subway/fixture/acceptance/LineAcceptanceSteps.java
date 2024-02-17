package subway.fixture.acceptance;

import static subway.utils.enums.Location.*;

import java.util.HashMap;
import java.util.List;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.line.LineCreateRequest;
import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.dto.section.SectionRequest;
import subway.dto.station.StationResponse;
import subway.fixture.line.LineRequestFixture;
import subway.fixture.section.SectionRequestFixture;
import subway.utils.enums.Location;
import subway.utils.rest.Rest;

public class LineAcceptanceSteps {
	
	public static LineResponse 노선_생성(
		String name,
		String color,
		Long upStationId,
		Long downStationId,
		Integer distance
	) {
		LineCreateRequest lineCreateRequest =
			LineRequestFixture.lineCreateRequest()
				.name(name)
				.color(color)
				.upStationId(upStationId)
				.downStationId(downStationId)
				.distance(distance)
				.build();

		return Rest.builder()
			.uri(Location.LINES.path())
			.body(lineCreateRequest)
			.post()
			.as(LineResponse.class);
	}

	public static LineResponse 노선_생성(Long upStationId, Long downStationId) {
		LineCreateRequest lineCreateRequest =
			LineRequestFixture.lineCreateRequest(upStationId, downStationId).build();

		return Rest.builder()
			.uri(Location.LINES.path())
			.body(lineCreateRequest)
			.post()
			.as(LineResponse.class);
	}

	public static LineResponse 노선_생성() {
		Long 강남역 = StationAcceptanceSteps.정류장_생성("강남역").as(StationResponse.class).getId();
		Long 양재역 = StationAcceptanceSteps.정류장_생성("양재역").as(StationResponse.class).getId();

		return 노선_생성(강남역, 양재역);
	}

	public static LineResponse 노선_조회(Long id) {
		String uri = LINES.path(id).toUriString();
		return Rest.builder()
			.get(uri)
			.as(LineResponse.class);
	}

	public static List<LineResponse> 모든_노선_조회() {
		String uri = LINES.path();
		return Rest.builder()
			.get(uri)
			.jsonPath()
			.getList("", LineResponse.class);
	}

	public static void 노선_수정(Long id, String changeName, String changeColor) {
		LineUpdateRequest request = new LineUpdateRequest(changeName, changeColor);

		String uri = LINES.path(id).toUriString();
		Rest.builder()
			.uri(uri)
			.body(request)
			.put();
	}

	public static void 노선_삭제(Long id) {
		String uri = LINES.path(id).toUriString();
		Rest.builder().delete(uri);
	}

	public static LineResponse 노선_구간_추가(Long id, Long upStationId, Long downStationId, Integer distance) {
		String uri = LINES.path(id).path("/sections").toUriString();
		SectionRequest sectionRequest = SectionRequestFixture.builder()
			.upStationId(upStationId)
			.downStationId(downStationId)
			.distance(distance)
			.build();

		return Rest.builder()
			.uri(uri)
			.body(sectionRequest)
			.post()
			.as(LineResponse.class);
	}

	public static ExtractableResponse<Response> 노선_구간_삭제(Long id, HashMap<String, String> params) {
		String uri = LINES.path(id).path("/sections").toUriString();
		return Rest.builder()
			.uri(uri)
			.delete(params);
	}
}
