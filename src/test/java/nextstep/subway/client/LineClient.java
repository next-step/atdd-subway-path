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
public class LineClient {

    private static final String LINES_PATH = "/lines";
    private static final String LINE_PATH = "/lines/{id}";
    private static final String SECTIONS_PATH = "/lines/{id}/sections";

    private final ApiCRUD apiCRUD;

    public ExtractableResponse<Response> createLine(LineCreationRequest lineRequest) {
        return apiCRUD.create(LINES_PATH, lineRequest);
    }

    public ExtractableResponse<Response> fetchLines() {
        return apiCRUD.read(LINES_PATH);
    }

    public ExtractableResponse<Response> fetchLine(Long lineId) {
        return apiCRUD.read(LINE_PATH, lineId);
    }

    public ExtractableResponse<Response> modifyLine(Long lineId, String name, String color) {
        LineModificationRequest lineRequest = new LineModificationRequest(name, color);
        return apiCRUD.update(LINES_PATH+ "/" + lineId, lineRequest);
    }

    public ExtractableResponse<Response> deleteLine(Long lineId) {
        return apiCRUD.delete(LINE_PATH, lineId);
    }

    public ExtractableResponse<Response> addSection(Long lineId, SectionRegistrationRequest sectionRequest) {
        return apiCRUD.create(LINES_PATH + "/" + lineId + "/sections", sectionRequest);
    }

    public ExtractableResponse<Response> removeSection(Long lineId, Long stationId) {
        return apiCRUD.delete(SECTIONS_PATH + "?stationId=" + stationId, lineId);
    }

}
