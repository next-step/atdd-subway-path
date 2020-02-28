package atdd.path.controller;

import atdd.AbstractAcceptanceTest;
import atdd.path.domain.dto.LineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AbstractAcceptanceTest {

    private LineHttpTest lineHttpTest;

    @BeforeEach
    public void setup() {
        this.lineHttpTest = new LineHttpTest(webTestClient);
    }

    @Test
    public void 지하철_노선_등록() {
        LineDto line = lineHttpTest.createLineTest().getResponseBody();

        assertThat(line.getName()).isEqualTo("2호선");
    }

    @Test
    public void 지하철_노선_목록_조회() {
        // given
        LineDto lineDto = lineHttpTest.createLineTest().getResponseBody();

        webTestClient.get().uri("/lines")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(LineDto.class)
                .hasSize(1)
                .contains(lineDto);
    }

    @Test
    public void 지하철_노선_정보_조회() {
        // given
        LineDto saveLine = lineHttpTest.createLineTest().getResponseBody();

        LineDto lines = lineHttpTest.findLineByTest(saveLine.getId()).getResponseBody();
        assertThat(lines.getName()).isEqualTo("2호선");
    }

    @Test
    public void 지하철_노선_삭제() {
        // given
        LineDto lineDto = lineHttpTest.createLineTest().getResponseBody();

        // when
        webTestClient.delete().uri("/lines/" + lineDto.getId())
                .exchange()
                .expectStatus().isNoContent();
    }
}
