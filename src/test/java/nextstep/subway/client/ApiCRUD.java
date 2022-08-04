package nextstep.subway.client;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public interface ApiCRUD {

    <T> ExtractableResponse<Response> create(String path, T jsonBody);

    ExtractableResponse<Response> read(String path);

    <T> ExtractableResponse<Response> read(String path, T... pathVariable);

    <T> ExtractableResponse<Response> update(String path, T jsonBody);

    <T> ExtractableResponse<Response> delete(String path, T pathVariable);

}
