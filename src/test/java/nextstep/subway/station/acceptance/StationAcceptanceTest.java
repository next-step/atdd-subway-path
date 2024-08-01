package nextstep.subway.station.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static nextstep.subway.util.StationStep.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    String 강남역 = "강남역";
    String 역삼역 = "역삼역";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        지하철_역_등록(강남역);

        // then
        List<String> stationNames = 지하철_역_전체_조회();
        assertThat(stationNames).containsAnyOf(강남역);
    }

    @DisplayName("2개의 지하철역을 생성한 다음, 2개의 지하철역을 조회한다.")
    @Test
    void showStation() {
        // given
        지하철_역_등록(강남역);
        지하철_역_등록(역삼역);

        // when
        var stationNames = 지하철_역_전체_조회();

        // then
        assertThat(stationNames.size()).isEqualTo(2);
        assertThat(stationNames).containsAll(List.of(강남역, 역삼역));
    }

    @DisplayName("지하철역을 생성한 다음, 해당 지하철역을 삭제한 뒤, 지하철역 목록을 조회하여 삭제된 것을 확인한다.")
    @Test
    void deleteStation() {
        // given
        var 강남역_생성응답 = 지하철_역_등록(강남역);
        var 역삼역_생성응답 = 지하철_역_등록(역삼역);

        var stationNamesAfterCreation = 지하철_역_전체_조회();

        assertThat(stationNamesAfterCreation.size()).isEqualTo(2);
        assertThat(stationNamesAfterCreation).containsAll(List.of(강남역, 역삼역));

        // when
        지하철_역_삭제(강남역_생성응답.getId());
        지하철_역_삭제(역삼역_생성응답.getId());

        // then
        var stationNamesAfterDeletion = 지하철_역_전체_조회();
        assertThat(stationNamesAfterDeletion).doesNotContainAnyElementsOf(List.of(강남역, 역삼역));
    }

}