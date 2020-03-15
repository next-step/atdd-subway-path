package atdd.path;

import atdd.AbstractAcceptanceTest;
import atdd.path.dto.LineResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AbstractAcceptanceTest {
    private LineHttpTest lineHttpTest;

    @BeforeEach
    void setUp() {
        this.lineHttpTest = new LineHttpTest(webTestClient);
    }

    @Test
    void create() {
        //when
        LineResponseView lineResponseView
                = lineHttpTest.create("2호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10);

        //then
        assertThat(lineResponseView.getId()).isEqualTo(1L);
        assertThat(lineResponseView.getName()).isEqualTo("2호선");
        assertThat(lineResponseView.getStartTime()).isBefore(lineResponseView.getEndTime());
        assertThat(lineResponseView.getIntervalTime()).isEqualTo(10);
    }

    @Test
    void findById() {
        //given
        Long lineId = lineHttpTest.create("2호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10).getId();

        //when
        LineResponseView lineResponseView = lineHttpTest.findById(lineId);

        //then
        assertThat(lineResponseView.getId()).isEqualTo(lineId);
        assertThat(lineResponseView.getName()).isEqualTo("2호선");
        assertThat(lineResponseView.getStartTime()).isBefore(lineResponseView.getEndTime());
    }

    @Test
    void findAll() {
        //given
        Long lineId = lineHttpTest.create("2호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10).getId();
        Long lineId2 = lineHttpTest.create("3호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10).getId();
        Long lineId3 = lineHttpTest.create("4호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10).getId();

        //when, then
        webTestClient.get().uri("/lines")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LineResponseView.class)
                .hasSize(3);
    }

    @Test
    void deleteById() {
        //given
        Long lineId = lineHttpTest.create("2호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10).getId();

        //when, then
        webTestClient.delete().uri("/lines/" + lineId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }
}
