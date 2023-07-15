package subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import subway.acceptance.AcceptanceTest;
import subway.acceptance.station.StationSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DisplayName("지하철노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    public List<Long> stationIds = new ArrayList<>();

    @BeforeEach
    void addLine() {
        List.of("강남역", "역삼역", "선릉역", "삼성역", "잠실역", "강변역", "건대역", "성수역", "왕십리역").forEach(StationSteps::역_생성_API);
        var response = StationSteps.역_목록_조회_API();
        stationIds = response.body().jsonPath().getList("id", Long.class);
    }

    // Week 1

    /**
     * When 노선을 생성하면
     * Then 상행역과 하행역이 포함된 기본 구간이 생성되고
     * Then 노선 조회로 상행역과 하행역이 포함된 기본 구간이 있다.
     */
    @DisplayName("기본 구간을 생성 한다.")
    @Test
    void createSection() {
        // when
        var 이호선_요청 = LineRequestGenerator.이호선_요청_만들기(stationIds.get(0), stationIds.get(1));
        var createLineResponse = LineSteps.노선_생성_API(이호선_요청);
        final String createdLocation = createLineResponse.header("Location");

        // then
        assertThat(createLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        var retrieveLinesResponse = LineSteps.노선_조회_API(createdLocation);
        List<String> stationNames = retrieveLinesResponse.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames.size()).isEqualTo(2);

    }

    /**
     * When 기본 노선의 구간이 있을 때
     * Then 노선의 하행역을 새로운 구간의 상행역으로 지정한 구간을 추가 한다.
     */
    @DisplayName("노선의 구간에 새로운 구간을 추가 한다.")
    @Test
    void appendStationToSection() {
        // when
        var 이호선_요청 = LineRequestGenerator.이호선_요청_만들기(stationIds.get(0), stationIds.get(1));
        var createLineResponse = LineSteps.노선_생성_API(이호선_요청);
        final String createdLocation = createLineResponse.header("Location");

        // then
        var 구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(1), stationIds.get(2), 10L);
        final String appendLocation = createdLocation + "/sections";
        var createSectionResponse = LineSteps.구간_추가_API(appendLocation, 구간_요청);
        LineSteps.노선_조회_API(createdLocation);

        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 3개의 역이 등록된 구간을 가진 노선이 있고
     * When 노선의 하행역을 제거하면
     * Then 구간이 삭제되고
     * Then 2개의 역을 가진 노선이 된다
     */
    @DisplayName("노선의 구간을 삭제한다.")
    @Test
    void deleteStationInSection() {
        // given
        final String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        final Long stationId = stationIds.get(2);
        var deleteSectionResponse = LineSteps.구간_제거_API(appendLocation, stationId);

        // then
        assertThat(deleteSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        var lineRetrieveResponse = LineSteps.노선_조회_API(createdLocation);
        List<String> stations = lineRetrieveResponse.jsonPath().getList("stations.name", String.class);
        assertThat(stations.size()).isEqualTo(2);
    }

    /**
     * Given 3개의 역이 등록된 구간을 가진 노선이 있고
     * When 노선의 두번째 역을 제거하면
     * Then 역이 제거되지 않는다.
     */
    @DisplayName("구간의 중간 역을 제거할 수 없다.")
    @Test
    void deleteStationInMiddleOfSection() {
        // given
        final String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        final Long stationId = stationIds.get(1);
        var deleteSectionResponse = LineSteps.구간_제거_API(appendLocation, stationId);

        // then
        assertThat(deleteSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선이 있고
     * When 노선의 역이 2개 이하 일 때
     * When 노선의 하행역을 제거하면
     * Then 역이 제거되지 않는다.
     */
    @DisplayName("노선의 구간이 1개 뿐일때 역을 제거할 수 없다.")
    @Test
    void deleteStationFromMinimalSection() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        final Long stationId = stationIds.get(2);
        LineSteps.구간_제거_API(appendLocation, stationId);

        // when
        final Long additionalDeleteLocation = stationIds.get(1);
        var deleteSectionResponse = LineSteps.구간_제거_API(appendLocation, additionalDeleteLocation);

        // then
        assertThat(deleteSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // Week 2

    /**
     * Given 노선이 있고
     * When 새로운 구간의 하행역을 기존 노선의 상행역으로 지정하면
     * Then 노선이 추가된다
     */
    @DisplayName("노선의 가장 앞에 새 구간을 추가한다.")
    @Test
    void appendSectionBeforeLine() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        var 상행에_추가하는_구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(3), stationIds.get(0), 10L);
        var response = LineSteps.구간_추가_API(appendLocation, 상행에_추가하는_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineSteps.노선_조회_API(createdLocation);

    }

    /**
     * Given 노선이 있고
     * When 새로운 구간의 상행역을 기존 노선의 하행역으로 지정하면
     * Then 노선이 추가된다
     */
    @DisplayName("노선의 가장 뒤에 새 구간을 추가한다.")
    @Test
    void appendSectionBehindLine() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        var 하행에_추가하는_구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(2), stationIds.get(3), 10L);
        var response = LineSteps.구간_추가_API(appendLocation, 하행에_추가하는_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineSteps.노선_조회_API(createdLocation);
    }

    /**
     * Given 노선이 있고
     * When 새로운 구간의 상행역과 기존 노선의 역이 같고 새로운 구간의 하행역은 노선에 없으면
     * Then 노선이 추가된다
     */
    @DisplayName("노선의 중간에 상행역이 같은 새 구간을 추가한다.")
    @Test
    void appendSectionNewUpStationExistUpStationIsInMiddleOfLine() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        var 노선_중간에_추가하는_구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(1), stationIds.get(3), 5L);
        var response = LineSteps.구간_추가_API(appendLocation, 노선_중간에_추가하는_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        var retrieveLine = LineSteps.노선_조회_API(createdLocation);
    }

    /**
     * Given 노선이 있고
     * When 새로운 구간의 하행역과 기존 노선의 역이 같고 새로운 구간의 상행역은 노선에 없으면
     * Then 노선이 추가된다
     */
    @DisplayName("노선의 중간에 하행역이 같은 새 구간을 추가한다.")
    @Test
    void appendSectionNewDownStationExistDownStationIsInMiddleOfLine() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        LineSteps.노선_조회_API(createdLocation);
        System.out.println("-------------------------");


        // when
        var 노선_중간에_추가하는_구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(3), stationIds.get(1), 5L);
        var response = LineSteps.구간_추가_API(appendLocation, 노선_중간에_추가하는_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        var retrieveLine = LineSteps.노선_조회_API(createdLocation);
    }

    /**
     * Given 노선이 있고
     * When 기존 구간 사이에 구간을 추가 할 때
     * When 새로운 구간의 길이가 기존 구간의 길이 이상이면
     * Then 노선에 추가되지 않는다.
     */
    @DisplayName("노선의 길이를 넘는 구간은 추가할 수 없다.")
    @Test
    void appendSectionOverLineDistance() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        var 노선_중간에_추가하는_구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(3), stationIds.get(1), 11L);
        var response = LineSteps.구간_추가_API(appendLocation, 노선_중간에_추가하는_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        var retrieveLine = LineSteps.노선_조회_API(createdLocation);
    }

    /**
     * Given 노선이 있고
     * When 기존 구간 사이에 구간을 추가 할 때
     * When 상행역과 하행역이 모두 노선의 구간에 존재하면
     * Then 노선에 추가되지 않는다.
     */
    @DisplayName("상행역과 하행역 모두 노선에 이미 존재하는 구간은 추가 할 수 없다.")
    @Test
    void appendSectionWithBothUpStationAndDownStationAlreadyExistInLine() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        var 이미_존재하는_구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(1), stationIds.get(2), 10L);
        var response = LineSteps.구간_추가_API(appendLocation, 이미_존재하는_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선이 있고
     * When 새로운 구간의 상행역과 하행역이 모두 기존 노선에 존재하지 않으면
     * Then 노선에 추가되지 않는다.
     * 상행역과 하행역이 모두 노선에 존재하지 않는 구간의 추가
     */
    @DisplayName("상행역과 하행역 모두가 노선에 존재하지 않는 구간은 추가 할 수 없다.")
    @Test
    void appendSectionWithNeitherUpStationNorDownStationExistInLine() {
        // given
        String createdLocation = 세구간이_포함된_노선_생성_작업();
        final String appendLocation = createdLocation + "/sections";

        // when
        var 존재하지_않는_구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(3), stationIds.get(4), 10L);
        var response = LineSteps.구간_추가_API(appendLocation, 존재하지_않는_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    private String 세구간이_포함된_노선_생성_작업() {
        var stringStringMap = LineRequestGenerator.이호선_요청_만들기(stationIds.get(0), stationIds.get(1));
        var 노선_생성 = LineSteps.노선_생성_API(stringStringMap);
        final String createdLocation = 노선_생성.header("Location");
        final String appendLocation = createdLocation + "/sections";

        var 구간_요청 = LineRequestGenerator.구간_요청_만들기(stationIds.get(1), stationIds.get(2), 10L);
        LineSteps.구간_추가_API(appendLocation, 구간_요청);

        return createdLocation;
    }



}

