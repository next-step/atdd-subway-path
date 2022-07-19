package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertUtils {

    public static void line(ExtractableResponse<Response> response, String name, String color) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ResponseUtils.getString(response, "name")).isEqualTo(name);
        assertThat(ResponseUtils.getString(response, "color")).isEqualTo(color);
    }

    public static void lines(ExtractableResponse<Response> response, String[] names, String[] colors) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ResponseUtils.getStringList(response, "name")).containsExactly(names);
        assertThat(ResponseUtils.getStringList(response, "color")).containsExactly(colors);
    }

    public static void lineSection(ExtractableResponse<Response> response, Long... stationIds) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ResponseUtils.getLongList(response, "stations.id")).containsExactly(stationIds);
    }
}
