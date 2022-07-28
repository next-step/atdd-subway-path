package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JsonResponseConverter {

    public Long convertToId(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getLong("id");
    }

    public List<Long> convertToIds(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList("id", Long.class);
    }

    public List<Long> convertToIds(List<ExtractableResponse<Response>> responses) {
        return responses.stream()
                .map(this::convertToId)
                .collect(Collectors.toList());
    }

    public String convertToName(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getString("name");
    }

    public List<String> convertToNames(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList("name", String.class);
    }

    public List<List> convertToList(ExtractableResponse<Response> response, String path) {
        return response.jsonPath()
                .getList(path, List.class);
    }

    public <T> T convert(ExtractableResponse<Response> response, String path, Class<T> type) {
        return response.jsonPath()
                .getObject(path, type);
    }

    public String convertToError(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getString("error");
    }

}
