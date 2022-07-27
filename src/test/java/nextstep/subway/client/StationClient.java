package nextstep.subway.client;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.client.dto.StationCreationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StationClient {

    private static final String STATIONS_PATH = "/stations";
    private static final String STATION_PATH = "/stations/{id}";

    private final ApiCRUD apiCRUD;

    public StationClient(ApiCRUD apiCRUD) {
        this.apiCRUD = apiCRUD;
    }

    public ExtractableResponse<Response> createStation(String stationName) {
        StationCreationRequest stationRequest = new StationCreationRequest(stationName);
        return apiCRUD.create(STATIONS_PATH, stationRequest);
    }

    public List<ExtractableResponse<Response>> createStations(List<String> stationNames) {
        return stationNames.stream()
                .map(this::createStation)
                .collect(Collectors.toList());
    }

    public ExtractableResponse<Response> fetchStations() {
        return apiCRUD.read(STATIONS_PATH);
    }

    public ExtractableResponse<Response> deleteStation(Long stationId) {
        return apiCRUD.delete(STATION_PATH, stationId);
    }

}
