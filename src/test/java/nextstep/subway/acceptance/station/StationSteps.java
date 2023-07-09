package nextstep.subway.acceptance.station;

import static nextstep.subway.acceptance.AcceptanceHelper.statusCodeShouldBe;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import nextstep.subway.applicaion.station.request.StationRequest;
import nextstep.subway.applicaion.station.response.StationResponse;

public class StationSteps {
    public static final String BASE_URL = "/stations";

    public static StationResponse 지하철역을_생성한다(final String name) {
        final var response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest(name))
                .when().post(BASE_URL)
                .then();

        statusCodeShouldBe(response, HttpStatus.CREATED);

        return response.extract()
                .jsonPath()
                .getObject("", StationResponse.class);
    }

    public static void 지하철역을_생성한다(final List<String> names) {
        names.forEach(StationSteps::지하철역을_생성한다);
    }

    public static List<StationResponse> 모든_지하철역을_조회한다() {
        final var response = RestAssured.given()
                .when().get(BASE_URL)
                .then();

        statusCodeShouldBe(response, HttpStatus.OK);

        return response.extract()
                .jsonPath()
                .getList("", StationResponse.class);
    }

    public static List<StationResponse> 지하철역을_조회한다(final String name) {
        return 모든_지하철역을_조회한다().stream()
                .filter(it -> it.getName().equals(name))
                .collect(Collectors.toUnmodifiableList());
    }

    public static void 지하철역을_제거한다(final Long stationId) {
        final var response = RestAssured.given()
                .when().delete(BASE_URL + "/" + stationId)
                .then();

        statusCodeShouldBe(response, HttpStatus.NO_CONTENT);
    }
}
