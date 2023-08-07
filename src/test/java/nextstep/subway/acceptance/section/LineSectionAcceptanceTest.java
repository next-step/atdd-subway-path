package nextstep.subway.acceptance.section;

import io.restassured.RestAssured;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.linesection.LineSectionRepository;
import nextstep.subway.linesection.LineSectionRequest;
import nextstep.subway.station.Station;
import org.apache.commons.lang3.RandomUtils;
import org.codehaus.groovy.transform.SourceURIASTTransformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.line.LineFixture.지하철_노선_생성_ID;
import static nextstep.subway.acceptance.line.LineFixture.지하철_노선_조회;
import static nextstep.subway.acceptance.section.LineSectionFixture.*;
import static nextstep.subway.acceptance.station.StationFixture.지하철역_생성_ID;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long firstStationId;
    private Long secondStationId;
    private Long thirdStationId;
    private Long fourthStationId;
    private Long fistLineId;

    @BeforeEach
    void beforeEach() {
        firstStationId = 지하철역_생성_ID("노원역");
        secondStationId = 지하철역_생성_ID("창동역");
        thirdStationId = 지하철역_생성_ID("강남역");
        fourthStationId = 지하철역_생성_ID("사당역");

        fistLineId = 지하철_노선_생성_ID(LineRequest.builder()
                .name("4호선")
                .color("light-blue")
                .upStationId(firstStationId)
                .downStationId(secondStationId)
                .distance(10)
                .build());
    }

    @DisplayName("지하철 구간 등록 테스트")
    @Nested
    class AddSectionTest {
        @DisplayName("정상 동작 경우")
        @Nested
        class Success {

            /**
             * Given 지하철역이 4개가 등록되어있다.
             * Given 지하철 노선이 1개가 등록되어있다. (노선 (Station: [first -> second])
             * Given 새로운 구간 요청( second -> third)
             * When 지하철 노선에 구간을 등록 요청한다
             * Then 지하철 노선 목록에 구간의 하행역 B가 추가 되었는지 확인한다.
             * And 지하철 노선
             */
            @DisplayName("지하철 노선에 구간끝에 구간을 등록")
            @Test
            void case_0() {
                //when
                지하철_구간_생성(fistLineId, 구간_생성_요청서(secondStationId, thirdStationId));
                //then
                LineResponse response = 지하철_노선_조회(fistLineId);
                assertThat(response.getStations().size()).isEqualTo(3);
                assertThat(response.getStations().get(2).getId()).isEqualTo(thirdStationId);
            }

            /**
             * Given 지하철역이 4개가 등록되어있다.
             * Given 지하철 노선이 1개가 등록되어있다. (노선 (Station: [first -> second -> fourth])
             * Given 새로운 구간 요청(second -> third)
             * When 지하철 노선에 구간을 등록 요청한다
             * Then 노선 first -> second -> third -> fourth 확인
             * And 지하철 노선
             */
            @DisplayName("지하철 노선 구간 중간에 구간을 등록")
            @Test
            void case_1() {
                //given
                지하철_구간_생성(fistLineId, 구간_생성_요청서(secondStationId, fourthStationId, 10));
                //when
                지하철_구간_생성(fistLineId, 구간_생성_요청서(secondStationId, thirdStationId, 4));
                //then
                LineResponse response = 지하철_노선_조회(fistLineId);
                assertThat(response.getStations().size()).isEqualTo(4);
                assertThat(response.getStations().get(0).getId()).isEqualTo(firstStationId);
                assertThat(response.getStations().get(1).getId()).isEqualTo(secondStationId);
                assertThat(response.getStations().get(2).getId()).isEqualTo(thirdStationId);
                assertThat(response.getStations().get(3).getId()).isEqualTo(fourthStationId);
            }

            /**
             * Given 지하철역이 4개가 등록되어있다.
             * Given 지하철 노선이 1개가 등록되어있다. (노선 (Station: [first -> second -> third])
             * Given 새로운 구간 요청( fourth -> first)
             * When 지하철 노선에 구간을 등록 요청한다
             * Then 노선 fourth -> first -> second -> third 확인
             */
            @DisplayName("지하철 노선 구간 앞에 구간을 등록")
            @Test
            void case_2() {
                //given
                지하철_구간_생성(fistLineId, 구간_생성_요청서(secondStationId, thirdStationId));
                //when
                지하철_구간_생성(fistLineId, 구간_생성_요청서(fourthStationId, firstStationId));
                //then
                LineResponse response = 지하철_노선_조회(fistLineId);
                assertThat(response.getStations().size()).isEqualTo(4);
                assertThat(response.getStations().get(0).getId()).isEqualTo(fourthStationId);
                assertThat(response.getStations().get(1).getId()).isEqualTo(firstStationId);
                assertThat(response.getStations().get(2).getId()).isEqualTo(secondStationId);
                assertThat(response.getStations().get(3).getId()).isEqualTo(thirdStationId);
            }
        }

        @DisplayName("실패 경우")
        @Nested
        class Fail {

        }
    }

    @DisplayName("지하철 구간 제거 테스트")
    @Nested
    class RemoveSectionTest {
        @DisplayName("정상 동작 경우")
        @Nested
        class Success {
            /**
             * Given 지하철역이 4개가 등록되어있다.
             * And 지하철 노선이 1개가 등록되어있다.
             * And 지하철 노선에 구간 2개를 추가해둔다. (노선 (Station: [first,second,third,fourth])
             * When 지하철 구간을 제거한다.
             * then 지하철 노선에서 해당 구간이 제거되어있는지 확인한다.
             */
            @DisplayName("지하찰_구간_끝역_삭제")
            @Test
            void case_0() {
                //given
                지하철_구간_생성(fistLineId, 구간_생성_요청서(secondStationId, thirdStationId));
                지하철_구간_생성(fistLineId, 구간_생성_요청서(thirdStationId, fourthStationId));
                //when
                지하철_구간_삭제(fistLineId, fourthStationId);
                //then
                assertThat(지하철역_ID_리스트_조회()).containsExactly(firstStationId, secondStationId, thirdStationId);
            }

            @DisplayName("지하찰_구간_중간역_삭제")
            @Test
            void case_1() {
                //given
                지하철_구간_생성(fistLineId, 구간_생성_요청서(secondStationId, thirdStationId));
                지하철_구간_생성(fistLineId, 구간_생성_요청서(thirdStationId, fourthStationId));
                //when
                지하철_구간_삭제(fistLineId, thirdStationId);
                //then
                assertThat(지하철역_ID_리스트_조회()).containsExactly(firstStationId, secondStationId, fourthStationId);
            }

            @DisplayName("지하찰_구간_첫번째역_삭제")
            @Test
            void case_2() {
                //given
                지하철_구간_생성(fistLineId, 구간_생성_요청서(secondStationId, thirdStationId));
                지하철_구간_생성(fistLineId, 구간_생성_요청서(thirdStationId, fourthStationId));
                //when
                지하철_구간_삭제(fistLineId, firstStationId);
                //then
                assertThat(지하철역_ID_리스트_조회()).containsExactly(secondStationId, thirdStationId, fourthStationId);
            }

            private List<Long> 지하철역_ID_리스트_조회() {
                List<Long> stationIdList = 지하철_노선_조회(fistLineId).getStations()
                        .stream()
                        .map(e -> e.getId())
                        .collect(Collectors.toList());
                return stationIdList;
            }
        }

        @DisplayName("실패 경우")
        @Nested
        class Fail {

            /**
             * Given 지하철역이 4개가 등록되어있다.
             * And 지하철 노선이 1개가 등록되어있다. (노선 (Station: [first,second])
             * When 지하철 구간을 제거한다.
             * Then 지하철 구간이 제거 되지 않고 예외를 발생시킨다.
             */
            @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없다")
            @Test
            void case_1() {
                //when
                //then
                지하철_구간_삭제_응답_상태값_체크(fistLineId, secondStationId, HttpStatus.BAD_REQUEST);
            }

            /**
             * first -> second 노선
             * fourth 역 삭제
             */
            @DisplayName("노선에 등록되어있지 않은 역을 제거하려 한다.")
            @Test
            void case_2() {
                //when
                //then
                지하철_구간_삭제_응답_상태값_체크(fistLineId,fourthStationId,HttpStatus.BAD_REQUEST);
            }
        }
    }

}