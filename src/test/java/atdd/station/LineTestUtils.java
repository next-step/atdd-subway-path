package atdd.station;

import atdd.HttpTestUtils;
import atdd.station.model.CreateEdgeRequestView;
import atdd.station.model.CreateLineRequestView;
import atdd.station.model.dto.LineResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class LineTestUtils {
    private final String LINES_PATH = "/lines";
    private final String EDGES_PATH = "/edge";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private static final ObjectMapper mapper = new ObjectMapper();

    private WebTestClient webTestClient;
    private HttpTestUtils httpTestUtils;


    public LineTestUtils(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
        this.httpTestUtils = new HttpTestUtils(webTestClient);
    }

    public LineResponseDto createLine(CreateLineRequestView createLineRequestView) {

        String inputJson = createLineRequestViewToJson(createLineRequestView);

        EntityExchangeResult result = httpTestUtils.postRequest(LINES_PATH, inputJson, LineResponseDto.class);

        LineResponseDto lineResponseDto = (LineResponseDto) result.getResponseBody();

        return lineResponseDto;
    }

    public List<LineResponseDto> findAll() {
        EntityExchangeResult result = httpTestUtils.getRequest(LINES_PATH, new ParameterizedTypeReference<List<LineResponseDto>>() {
        });

        return (List<LineResponseDto>) result.getResponseBody();
    }

    public LineResponseDto findById(final long id) {
        EntityExchangeResult result = httpTestUtils.getRequest(LINES_PATH + "/" + id, new ParameterizedTypeReference<LineResponseDto>() {
        });

        return (LineResponseDto) result.getResponseBody();
    }

    public void deleteLine(final long id) {
        httpTestUtils.deleteRequest(LINES_PATH + "/" + id);
    }

    public LineResponseDto addEdge(final long lineId, final long sourceStationId, final long targetStationId) {
        CreateEdgeRequestView createEdgeRequestView = new CreateEdgeRequestView();
        createEdgeRequestView.setSourceStationId(sourceStationId);
        createEdgeRequestView.setTargetStationId(targetStationId);

        EntityExchangeResult result = httpTestUtils.postRequest(LINES_PATH + "/" + lineId + EDGES_PATH, writeValueAsString(createEdgeRequestView), LineResponseDto.class);

        return (LineResponseDto) result.getResponseBody();
    }

    public void deleteEdge(final long id, final long stationId) {
        httpTestUtils.deleteRequest(LINES_PATH + "/" + id + EDGES_PATH + "?stationId=" + stationId);
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

    private String createLineRequestViewToJson(CreateLineRequestView createLineRequestView) {
        return "{\"name\":\"" + createLineRequestView.getName() +
                "\",\"startTime\":\"" + createLineRequestView.getStartTime().format(formatter) +
                "\",\"endTime\":\"" + createLineRequestView.getEndTime().format(formatter) +
                "\",\"intervalTime\":" + createLineRequestView.getIntervalTime() + "}";
    }
}
