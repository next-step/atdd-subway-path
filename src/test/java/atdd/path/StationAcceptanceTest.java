package atdd.path;

import atdd.AbstractAcceptanceTest;
import atdd.path.dto.StationResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AbstractAcceptanceTest {
    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
    }


    @Test
    void createTest() {
        //when
        Long stationId = stationHttpTest.create("강남");

        //then
        assertThat(stationId).isEqualTo(1L);
    }

    @Test
    void findByIdTest(){
        //given
        Long stationId = stationHttpTest.create("강남");

        //when
        Long id = stationHttpTest.findById(stationId);

        //then
        assertThat(stationId).isEqualTo(id);
    }

    @Test
    void findAllTest(){
        //given
        stationHttpTest.create("강남");
        stationHttpTest.create("역삼");
        stationHttpTest.create("선릉");

        //when
        webTestClient.get().uri("/stations")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StationResponseView.class)
                .hasSize(3);
    }

    @Test
    void deleteByIdTest(){
        //given
        Long stationId = stationHttpTest.create("강남");

        //when, then
        webTestClient.delete().uri("/stations/"+stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }
}
