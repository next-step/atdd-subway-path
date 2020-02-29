package atdd.station;

import atdd.HttpTestUtils;
import atdd.station.model.dto.CreateStationRequestView;
import atdd.station.model.dto.StationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

public class StationTestUtils {
    private final String STATIONS_PATH = "/stations";

    private static final ObjectMapper mapper = new ObjectMapper();
    private WebTestClient webTestClient;
    private HttpTestUtils httpTestUtils;

    public StationTestUtils(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
        this.httpTestUtils = new HttpTestUtils(webTestClient);
    }

    public StationDto createStation(String name) {
        String inputJson = writeValueAsString(CreateStationRequestView.builder()
                .name(name)
                .build());

        EntityExchangeResult result = httpTestUtils.postRequest(STATIONS_PATH, inputJson, StationDto.class);

        return (StationDto) result.getResponseBody();
    }

    public List<StationDto> findAll() {
        EntityExchangeResult result = httpTestUtils.getRequest(STATIONS_PATH, new ParameterizedTypeReference<List<StationDto>>() {
        });

        return (List<StationDto>) result.getResponseBody();
    }

    public StationDto findById(final long stationId) {
        EntityExchangeResult result = httpTestUtils.getRequest(STATIONS_PATH + "/" + stationId, new ParameterizedTypeReference<StationDto>() {
        });

        return (StationDto) result.getResponseBody();
    }

    public void deleteById(final long id) {
        httpTestUtils.deleteRequest(STATIONS_PATH + "/" + id);
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
