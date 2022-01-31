package nextstep.subway.acceptance;

import io.restassured.RestAssured;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();
    }


    public void 상태_값_검사(ExtractableResponse<Response> 응답_값, HttpStatus 상태_코드) {
        assertThat(응답_값.statusCode()).isEqualTo(상태_코드.value());
    }

    public void 리스트_값_검사(ExtractableResponse<Response> 응답_값, String 예상_키, String... 예상_값) {
        for (String 값 : 예상_값) {
            assertThat(응답_값.jsonPath().getList(예상_키)).contains(값);
        }
    }

    public void 단일_값_검사(ExtractableResponse<Response> 응답_값, String 예상_키, String 예상_값) {
        Object result = 응답_값.jsonPath().get(예상_키);
        assertThat(String.valueOf(result)).isEqualTo(예상_값);
    }

    public void 예외_검사(ExtractableResponse<Response> 응답_값, String 예외_메시지) {
        assertThat(응답_값.jsonPath().getString("message")).isEqualTo(예외_메시지);
    }
}
