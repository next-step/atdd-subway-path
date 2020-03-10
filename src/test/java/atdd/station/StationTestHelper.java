package atdd.station;

import atdd.HttpTestHelper;
import atdd.station.model.dto.CreateStationRequestView;
import atdd.station.model.dto.StationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

public class StationTestHelper {
    private final String STATIONS_PATH = "/stations";

    private static final ObjectMapper mapper = new ObjectMapper();
    private WebTestClient webTestClient;
    private HttpTestHelper httpTestHelper;

    public StationTestHelper(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
        this.httpTestHelper = new HttpTestHelper(webTestClient);
    }

    public StationDto createStation(String name) {
        String inputJson = writeValueAsString(CreateStationRequestView.builder()
                .name(name)
                .build());

        EntityExchangeResult result = httpTestHelper.postRequest(STATIONS_PATH, inputJson, StationDto.class);

        return (StationDto) result.getResponseBody();
    }

    public List<StationDto> findAll() {
        EntityExchangeResult result = httpTestHelper.getRequest(STATIONS_PATH, new ParameterizedTypeReference<List<StationDto>>() {
        });

        return (List<StationDto>) result.getResponseBody();
    }

    public StationDto findById(final long stationId) {
        EntityExchangeResult result = httpTestHelper.getRequest(STATIONS_PATH + "/" + stationId, new ParameterizedTypeReference<StationDto>() {
        });

        return (StationDto) result.getResponseBody();
    }

    public void deleteById(final long id) {
        httpTestHelper.deleteRequest(STATIONS_PATH + "/" + id);
    }

    public String writeValueAsString(Object object) {
        String result = null;
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
