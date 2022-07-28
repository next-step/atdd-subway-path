package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class HttpStatusValidator {

    public void validateCreated(ExtractableResponse<Response> response) {
        validateStatusCode(response, HttpStatus.CREATED);
    }

    public void validateOk(ExtractableResponse<Response> response) {
        validateStatusCode(response, HttpStatus.OK);
    }

    public void validateNoContent(ExtractableResponse<Response> response) {
        validateStatusCode(response, HttpStatus.NO_CONTENT);
    }

    public void validateBadRequest(ExtractableResponse<Response> response) {
        validateStatusCode(response, HttpStatus.BAD_REQUEST);
    }


    private void validateStatusCode(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

}
