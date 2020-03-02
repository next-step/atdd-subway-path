package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.LineResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AbstractAcceptanceTest {
    public static final String LINE_BASE_URI = "/lines";
    public static final String LINE_NAME = "사당역";
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
    }

    @Test
    void create() throws Exception {
        //when
        LineResponseView responseView = lineHttpTest.create(LINE_NAME);

        //then
        assertThat(responseView.getId()).isEqualTo(1L);
        assertThat(responseView.getName()).isEqualTo(LINE_NAME);
    }
}
