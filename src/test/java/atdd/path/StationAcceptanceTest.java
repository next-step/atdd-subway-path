package atdd.path;

import atdd.BaseAcceptanceTest;
import atdd.domain.Station;
import atdd.dto.StationResponseView;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationAcceptanceTest extends BaseAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);
    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        stationHttpTest = new StationHttpTest(webTestClient);
    }

    @Test
    void 인수테스트_지하철역_등록하기() {
        //when
        Long stationId = stationHttpTest.createStation("강남");

        //then
        assertThat(stationId).isEqualTo(1L);
    }

    @Test
    void 인수테스트_지하철역_조회하기(){
        //given
        Long stationId = stationHttpTest.createStation("강남");

        //when
        Station station = stationHttpTest.findById(stationId);

        //then
        assertThat(station.getId()).isEqualTo(stationId);
        assertThat(station.getName()).isEqualTo("강남");
    }

    @Test
    void 인수테스트_지하철역_삭제하기(){
        //given
        Long stationId = stationHttpTest.createStation("강남");

        //when, then
        webTestClient.delete().uri("/stations/"+stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }
}
