package nextstep.subway.acceptance.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public class ResponseHelper<T> {
    public ExtractableResponse<Response> response;
    public TypeReference<T> responseType;

    public ResponseHelper(ExtractableResponse<Response> response, TypeReference<T> responseType) {
        this.response = response;
        this.responseType = responseType;
    }

    public T toResponse() {
        return this.response.as(responseType.getType());
    }

    public boolean isBadRequest() {
        return response.statusCode() == HttpStatus.BAD_REQUEST.value();
    }

    public boolean isCreated() {
        return response.statusCode() == HttpStatus.CREATED.value();
    }

    public boolean isOk() {
        return response.statusCode() == HttpStatus.OK.value();
    }

    public boolean isNoContent() {
        return response.statusCode() == HttpStatus.NO_CONTENT.value();
    }
}
