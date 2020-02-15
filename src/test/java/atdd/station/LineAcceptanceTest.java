package atdd.station;

import atdd.station.model.CreateLineRequestView;
import atdd.station.model.entity.Line;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createLine() {
        //when
        CreateLineRequestView createLineRequestView = CreateLineRequestView.builder()
                .name("2호선")
                .startTime(LocalTime.of(5, 0))
                .endTime(LocalTime.of(23, 50))
                .intervalTime(10)
                .build();

        Line line = createLine(createLineRequestView);

        // then
        Line actualLine = createLineRequestView.toLine();
        actualLine.setId(line.getId());

        String expected = writeValueAsString(line);
        String actual = writeValueAsString(actualLine);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void findAllLines() {
        // given
        Line line = given("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10);

        // when
        EntityExchangeResult result = webTestClient.get().uri("/lines")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(List.class).returnResult();

        //then
        String expected = writeValueAsString(result.getResponseBody());
        String actual = writeLineListAsString(Arrays.asList(line));

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void findLine() {
        // given
        Line line = given("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10);

        // when
        EntityExchangeResult result = webTestClient.get().uri("/lines/" + line.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Line.class).returnResult();

        // then
        String expected = writeValueAsString(result.getResponseBody());
        String actual = writeValueAsString(line);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void deleteLine() {
        // given
        long lineId = given("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10).getId();

        // when
        EntityExchangeResult result = webTestClient.delete().uri("/lines/" + lineId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().returnResult();

        // then
        Optional<Line> optionalLine = findById(lineId);
        Line line = optionalLine.isPresent() ? optionalLine.get() : null;

        assertThat(line).isNull();
    }

    private Line given(final String name,
                       final LocalTime startTime,
                       final LocalTime endTime,
                       final int intervalTime) {
        CreateLineRequestView createLineRequestView = CreateLineRequestView.builder()
                .name(name)
                .startTime(startTime)
                .endTime(endTime)
                .intervalTime(intervalTime)
                .build();

        return createLine(createLineRequestView);
    }

    private Line createLine(CreateLineRequestView createLineRequestView) {

        String inputJson = createLineRequestViewToJson(createLineRequestView);

        EntityExchangeResult result = webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectBody(Line.class).returnResult();

        Line line = (Line) result.getResponseBody();

        return line;
    }

    private Optional<Line> findById(final long id) {
        EntityExchangeResult result = webTestClient.get().uri("/lines/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(Line.class).returnResult();

        return Optional.ofNullable((Line) result.getResponseBody());
    }

    private String createLineRequestViewToJson(CreateLineRequestView createLineRequestView) {
        return "{\"name\":\"" + createLineRequestView.getName() +
                "\",\"startTime\":\"" + createLineRequestView.getStartTime().format(formatter) +
                "\",\"endTime\":\"" + createLineRequestView.getEndTime().format(formatter) +
                "\",\"intervalTime\":" + createLineRequestView.getIntervalTime() + "}";
    }

    private String writeLineListAsString(final List<Line> lines){
        final StringBuilder stringBuilder = new StringBuilder();
        final String lineValue = "{\"id\":%d" +
                ",\"name\":\"%s" +
                "\",\"startTime\":\"%s" +
                "\",\"endTime\":\"%s" +
                "\",\"intervalTime\":%d" +
                ",\"stations\":%s}";

        for (Line line : lines) {
            if(stringBuilder.length() > 0)
                stringBuilder.append(",");

            stringBuilder.append(String.format(lineValue,
                    line.getId(),
                    line.getName(),
                    line.getStartTime().format(formatter),
                    line.getEndTime().format(formatter),
                    line.getIntervalTime(),
                    writeValueAsString(line.getStations())));
        }

        return "[" + stringBuilder.toString() + "]";
    }

    private String writeValueAsString(Object object) {
        String result = null;
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
