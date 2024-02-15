package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import nextstep.subway.dto.LineResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseAcceptanceTest {
    public static final Map<String, String> 역삼역 = Map.of("name", "역삼역");
    public static final Map<String, String> 선릉역 = Map.of("name", "선릉역");
    public static final Map<String, String> 강남역 = Map.of("name", "강남역");
    public static final Map<String, String> 왕십리역 = Map.of("name", "왕십리역");
    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();
    }

    void createStation(Map<String, String> param1) {
        given().body(param1)
               .contentType(MediaType.APPLICATION_JSON_VALUE).log().all()
               .when().post("/stations")
               .then().log().all();
    }

    LineResponse createLine(Map<String, String> requestParam) {
        return given().body(requestParam)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .when().post("/lines").then().log().all().extract()
                      .jsonPath().getObject(".", LineResponse.class);
    }

    public Map<String, String> getRequestParam_신분당선() {
        String lineName = "신분당선";
        String lineColor = "bg-red-600";
        long upStationId = 1L;
        long downStationId = 2L;
        Integer distance = 10;

        return Map.of(
            "name", lineName,
            "color", lineColor,
            "upStationId", Long.toString(upStationId),
            "downStationId", Long.toString(downStationId),
            "distance", distance.toString()
        );
    }

    public Map<String, String> getRequestParam_분당선() {
        String lineName = "분당선";
        String lineColor = "bg-yellow-600";
        Long upStationId = 3L;
        Long downStationId = 4L;
        Integer distance = 2;

        return Map.of(
            "name", lineName,
            "color", lineColor,
            "upStationId", upStationId.toString(),
            "downStationId", downStationId.toString(),
            "distance", distance.toString()
        );
    }

}
