package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AbstractAcceptanceTest {
    public static final String LINE_BASE_URI = "/lines";
    public static final String LINE_NAME = "2호선";
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    public static final LocalTime START_TIME = LocalTime.of(5, 00);
    public static final LocalTime END_TIME = LocalTime.of(23, 50);
    public static final int INTERVAL_TIME = 10;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
    }

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void create() throws Exception {
        //given
        LineRequestView requestView = LineRequestView.builder()
                .name(LINE_NAME)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        String inputJson = objectMapper.writeValueAsString(requestView);

        //when
        LineResponseView responseView = lineHttpTest.create(inputJson);

        //then
        assertThat(responseView.getId()).isEqualTo(1L);
        assertThat(responseView.getName()).isEqualTo(LINE_NAME);
    }
}
