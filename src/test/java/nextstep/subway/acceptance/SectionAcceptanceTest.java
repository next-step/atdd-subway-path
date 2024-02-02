package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import nextstep.subway.utils.LineFactory;
import nextstep.subway.utils.SectionFactory;
import nextstep.subway.utils.StationFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SectionAcceptanceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {


        StationFactory.createStation("banghwa");
        StationFactory.createStation("gangdong");
        StationFactory.createStation("macheon");
        LineFactory.createLine("5호선", 1L, 2L);
    }

    /**
     * Given 지하철노선에 등록되어 있는 하행종점역을 상행역으로 하는 구간을 노선에 추가하고
     * When 지하철노선을 조회하면
     * Then 순서대로 방화역 - 강동역 - 마천역이 조회된다
     */
    @DisplayName("지하철노선 구간 마지막에 추가 성공")
    @Test
    void addSectionSuccess() {
        // given
        ExtractableResponse<Response> response = SectionFactory.createSection(1L, 2L, 3L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        response = LineFactory.getLines();

        // then
        List<String> stationNames = response.jsonPath().getList("[0].stations.name", String.class);
        assertThat(stationNames).containsExactly("banghwa", "gangdong", "macheon");
    }

    /**
     * Given 지하철노선에 등록되어 있는 상행종점역을 하행역으로 하는 구간(마천 - 방화)을 노선에 추가하고
     * When 지하철노선을 조회하면
     * Then 순서대로 마천역 - 방화역 - 강동역이 조회된다
     */
    @DisplayName("지하철노선 구간 처음에 추가 성공")
    @Test
    void addSectionFirstSuccess() {
        // given
        Long newUpstationId = 3L;
        Long oldUpstationId = 1L;
        SectionFactory.createSection(1L, newUpstationId, oldUpstationId);

        // when
        ExtractableResponse<Response> response = LineFactory.getLines();

        // then
        List<String> stationNames = response.jsonPath().getList("[0].stations.name", String.class);
        assertThat(stationNames).containsExactly("macheon", "banghwa", "gangdong");
    }

    /**
     * Given 상행종점역과 하행종점역 사이에 상행종점역을 상행역으로 하는 구간을 추가하고(방화 - 강동 사이에 마천을 추가)
     * When 지하철노선을 조회하면
     * Then 순서대로 방화역 - 마천역 - 강동역이 조회된다
     */
    @DisplayName("지하철노선 구간 중간에 추가 성공")
    @Test
    void insertSectionSuccess() {
        // given
        Long upstationId = 1L;
        Long middleStationId = 3L;
        SectionFactory.createSection(1L, upstationId, middleStationId);

        // when
        ExtractableResponse<Response> response = LineFactory.getLines();

        // then
        List<String> stationNames = response.jsonPath().getList("[0].stations.name", String.class);
        assertThat(stationNames).containsExactly("banghwa", "macheon", "gangdong");
    }

    /**
     * Given 지하철노선에 등록되어 있는 하행종점역을 상행역으로 하지 않는 구간을 노선에 추가하려고 한다
     * When 이러한 구간을 노선에 추가하려고 하면
     * Then 이 요청은 실패해야 한다
     */
//    @DisplayName("하행종점역을 상행역으로 하지 않는 구간 추가 실패")
//    @Test
//    void addSectionFailWhenUpstationNotDownstationofLine() {
//        // given
//        Long wrongUpstationId = 3L;
//        Long downstationId = 2L;
//
//        // when
//        ExtractableResponse<Response> response = SectionFactory.createSection(1L, wrongUpstationId, downstationId);
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
//        assertThat(response.body().asString()).contains("해당 노선의 하행 종점역과 새로운 구간의 상행역이 일치해야 합니다.");
//
//    }

    /**
     * Given 해당 노선에 등록되어있는 역을 새 구간의 하행역으로 해서 추가하려고 한다
     * When 이러한 구간을 노선에 추가하려고 하면
     * Then 이 요청은 실패해야 한다
     */
    @DisplayName("노선에 등록되어있는 역을 하행역으로 하는 구간 실패")
    @Test
    void addSectionFailWhenNewDownstationAlreadyOnLine() {
        // given
        Long upstationId = 2L;
        Long existingStationId = 1L;

        // when
        ExtractableResponse<Response> response = SectionFactory.createSection(1L, upstationId, existingStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("새로운 구간의 하행역은 이미 노선에 존재하는 역이면 안 됩니다.");
    }

    /**
     * Given 구간을 생성하고
     * When 구간을 삭제하면
     * Then 노선을 조회했을 때 삭제한 구간을 찾을 수 없다
     */
    @DisplayName("지하철노선 구간 삭제 성공")
    @Test
    void sectionDeleteSuccess() {
        // given
        SectionFactory.createSection(1L, 2L, 3L);

        // when
        ExtractableResponse<Response> response = SectionFactory.deleteSection(1L, 3L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        response = LineFactory.getLines();
        List<String> stationIds = response.jsonPath().getList("[0].stations.id", String.class);
        assertThat(stationIds).hasSize(2);
        assertThat(stationIds.get(1)).isEqualTo("2");
        assertThat(stationIds).doesNotContain("3");
    }

    /**
     * Given 구간을 생성하고
     * When 마지막 구간이 아닌 구간을 삭제하려고 할 때
     * Then 이 요청은 실패한다
     */
    @DisplayName("마지막 구간이 아닌 구간 삭제 실패")
    @Test
    void NonLastSectionDeleteFail() {
        // given
        SectionFactory.createSection(1L, 2L, 3L);

        // when
        ExtractableResponse<Response> response = SectionFactory.deleteSection(1L, 2L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("노선에 등록된 하행 종점역만 제거할 수 있습니다.");

    }

    /**
     * Given 상행 종점역과 하행 종점역만 있는 노선에서
     * When 마지막 구간을 삭제하려고 할 때
     * Then 이 요청은 실패한다
     */
    @DisplayName("종점역만 존재하는 노선의 구간 삭제 실패")
    @Test
    void sectionDeleteFailInMinimalLine() {
        // when
        ExtractableResponse<Response> response = SectionFactory.deleteSection(1L, 2L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("노선에 상행 종점역과 하행 종점역만 있는 경우에는 제거할 수 없습니다.");

    }

}
