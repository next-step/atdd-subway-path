package nextstep.subway.steps;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.SectionCreateRequest;

public class SectionSteps {

	public static ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
		SectionCreateRequest sectionRequest = new SectionCreateRequest(upStationId, downStationId, distance);

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines/{id}/sections", lineId)
			.then().log().all()
			.extract();

		return response;
	}

	public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.param("stationId", stationId)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().delete("/lines/{id}/sections", lineId)
			.then().log().all()
			.extract();

		return response;
	}
}
