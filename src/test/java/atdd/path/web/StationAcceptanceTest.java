package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.StationResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AbstractAcceptanceTest {
    private String STATION_NAME = "사당";
    private String STATION_NAME_2 = "방배";
    private String STATION_NAME_3 = "서초";
    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
    }

    @DisplayName("지하철역 등록하기")
    @Test
    public void create() {
        // when
        StationResponseView responseView = stationHttpTest.create(STATION_NAME);

        // then
        assertThat(responseView.getId()).isEqualTo(1L);
    }

    @DisplayName("지하철역 삭제하기")
    @Test
    public void delete() {
        //given
        StationResponseView responseView = stationHttpTest.create(STATION_NAME);

        //when, then
        webTestClient.delete().uri("/stations/" + responseView.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @DisplayName("지하철역 한 개 정보 조회하기")
    @Test
    public void findById(){
        //given
        StationResponseView responseView = stationHttpTest.create(STATION_NAME);

        //when
        StationResponseView stationById = stationHttpTest.findById(responseView.getId());

        //then
        assertThat(stationById.getName()).isEqualTo(STATION_NAME);
    }

    @DisplayName("지하철역 목록 정보 조회하기")
    @Test
    public void showAll(){
        //given
        int theNumberOfStations = 3;
        stationHttpTest.create(STATION_NAME);
        stationHttpTest.create(STATION_NAME_2);
        stationHttpTest.create(STATION_NAME_3);

        //when
        List<StationResponseView> stationResponseViews = stationHttpTest.showAll();

        //then
        assertThat(stationResponseViews.size()).isEqualTo(theNumberOfStations);
        assertThat(stationResponseViews.get(theNumberOfStations-1).getName())
                .isEqualTo(STATION_NAME_3);
    }
}
