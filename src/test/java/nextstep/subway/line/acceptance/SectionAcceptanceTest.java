package nextstep.subway.line.acceptance;

import static nextstep.subway.line.acceptance.LineSectionAcceptanceTest.*;
import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선의 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private LineResponse 신분당선;
	private StationResponse 강남역;
	private StationResponse 양재역;
	private StationResponse 정자역;
	private StationResponse 광교역;

	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
		양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
		정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
		광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);

		Map<String, String> lineCreateParams;
		lineCreateParams = new HashMap<>();
		lineCreateParams.put("name", "신분당선");
		lineCreateParams.put("color", "bg-red-600");
		lineCreateParams.put("upStationId", 강남역.getId() + "");
		lineCreateParams.put("downStationId", 양재역.getId() + "");
		lineCreateParams.put("distance", 10 + "");
		신분당선 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);
	}

	@DisplayName("지하철 노선 중간에 역을 등록한다.")
	@Test
	void addSectionInMiddle() {
		// when
		지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 정자역, 6);

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		지하철_노선에_지하철역_등록됨(response);
		지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 정자역, 양재역));
	}

	@DisplayName("지하철 노선의 중간에 위치한 역을 삭제한다.")
	@Test
	void removeSectionInMiddle() {
		// given
		지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 정자역, 6);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_제외_요청(신분당선, 정자역);

		// then
		지하철_노선에_지하철역_제외됨(response);
	}

	@DisplayName("지하철 노선에 포함되지 않은 역을 삭제한다.")
	@Test
	void removeSectionNotIncluded() {
		// given

		// when

		// then
	}

	@DisplayName("지하철 노선에서 마지막 구간을 제거한다.")
	@Test
	void removeSectionLast() {
		// given

		// when

		// then
	}
}
