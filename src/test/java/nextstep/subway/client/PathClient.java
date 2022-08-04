package nextstep.subway.client;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import nextstep.subway.client.dto.LineCreationRequest;
import nextstep.subway.client.dto.LineModificationRequest;
import nextstep.subway.client.dto.SectionRegistrationRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PathClient {

    private static final String SHORTEST_PATHS_PATH = "/paths?source=%s&target=%s";

    private final ApiCRUD apiCRUD;

    public ExtractableResponse<Response> getShortestPath(Long source, Long target) {
        return apiCRUD.read(String.format(SHORTEST_PATHS_PATH, source, target));
    }

}
