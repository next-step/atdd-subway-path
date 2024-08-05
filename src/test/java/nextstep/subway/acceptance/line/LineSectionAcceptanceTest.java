package nextstep.subway.acceptance.line;

import static nextstep.subway.acceptance.line.LineUtils.*;
import static nextstep.subway.acceptance.line.SectionUtils.*;
import static nextstep.subway.acceptance.station.StationUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("구간 관리 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private Long 신사역_id;
    private Long 논현역_id;
    private Long 신논현역_id;
    private Long 강남역_id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        신사역_id = 지하철역_생성_후_id_추출(신사역);
        논현역_id = 지하철역_생성_후_id_추출(논현역);
        신논현역_id = 지하철역_생성_후_id_추출(신논현역);
        강남역_id = 지하철역_생성_후_id_추출(강남역);
    }

    @DisplayName("addSection 테스트")
    @Nested
    class AddSectionTest {

        /**
         * Given 지하철 노선이 주어지고 When 하행 종착역에 새로운 구간을 등록하면 Then 구간이 등록된다.
         */
        @DisplayName("하행 종착역에 새로운 구간을 생성한다.")
        @Test
        void createSection1() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

            // when
            ExtractableResponse<Response> 지하철구간_생성_응답 = 지하철구간_생성(신분당선_id, 논현역_id, 신논현역_id, 10L);

            // then
            assertThat(지하철구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        /**
         * Given 지하철 노선이 주어지고 When 상행종착역에 새로운 구간을 등록하면 Then 구간이 등록된다.
         */
        @DisplayName("상행 종착역에 새로운 구간을 생성한다.")
        @Test
        void createSection2() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

            // when
            ExtractableResponse<Response> 지하철구간_생성_응답 = 지하철구간_생성(신분당선_id, 강남역_id, 신사역_id, 10L);

            // then
            assertThat(지하철구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        /**
         * Given 지하철 노선이 주어지고 When 지하철 노선 가운데 새로운 구간을 등록하면 Then 구간이 등록된다.
         */
        @DisplayName("지하철 노선 가운데 새로운 구간을 생성한다.")
        @Test
        void createSection3() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

            // when
            ExtractableResponse<Response> 지하철구간_생성_응답 = 지하철구간_생성(신분당선_id, 신사역_id, 신논현역_id, 5L);

            // then
            assertThat(지하철구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        /**
         * Given 지하철 노선이 주어지고 When 새로운 구간을 생성할 때 새로운 구간의 상행역이 노선에 등록되어 있지 않고 하행역이 상행 종착역이 아니면 Then
         * 예외가 발생한다.
         */
        @DisplayName("새로운 구간의 상행역이 노선에 등록되어 있지 않고 하행역이 상행 종착역이 아니면 예외가 발생한다.")
        @Test
        void createSectionException() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

            // when
            ExtractableResponse<Response> 지하철구간_생성_응답 = 지하철구간_생성(신분당선_id, 신논현역_id, 강남역_id, 10L);

            // then
            assertThat(지하철구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        }

        /**
         * Given 지하철 노선이 주어지고 When 새로운 구간을 생성할 때 새로운 구간이 이미 등록되어 있으면 Then 예외가 발생한다.
         */
        @DisplayName("새로운 구간이 이미 등록되어 있으면 예외가 발생한다.")
        @Test
        void createSectionException4() {
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

            // when
            ExtractableResponse<Response> 지하철구간_생성_응답 = 지하철구간_생성(신분당선_id, 신사역_id, 논현역_id, 10L);

            // then
            assertThat(지하철구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * Given 지하철 노선이 주어지고 When 새로운 구간을 생성할 때 새로운 구간의 상행역이 기존 상행역에 존재하고, 하행역도 기존 노선에 존재하면 Then
         * 예외가 발생한다.
         */
        @DisplayName("새로운 구간의 상행역이 기존 상행역에 존재하고, 하행역도 기존 노선에 존재하면 예외가 발생한다.")
        @Test
        void createSectionException6() {
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);
            지하철구간_생성(신분당선_id, 논현역_id, 강남역_id, 5L);

            // when
            ExtractableResponse<Response> 지하철구간_생성_응답 = 지하철구간_생성(신분당선_id, 논현역_id, 신사역_id, 5L);

            // then
            assertThat(지하철구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * Given 지하철 노선이 주어지고 When 새로운 구간을 생성할 때 새로운 구간의 길이가 기존 구간보다 길거나 같으면 Then 예외가 발생한다.
         */
        @DisplayName("새로운 구간의 길이가 기존 구간보다 길거나 같으면 예외가 발생한다.")
        @Test
        void createSectionException5() {
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 신논현역_id, 10L);

            // when
            ExtractableResponse<Response> 지하철구간_생성_응답 = 지하철구간_생성(신분당선_id, 신사역_id, 논현역_id, 20L);

            // then
            assertThat(지하철구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

    }

    @DisplayName("removeSection 테스트")
    @Nested
    class RemoveSectionTest {

        /**
         * Given 지하철 구간이 주어지고 When 노선에 등록된 상행 종점역을 삭제하면 Then 지하철 구간이 삭제된다.
         */
        @DisplayName("노선에 등록된 상행 종점역을 삭제한다.")
        @Test
        void deleteSection() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);
            지하철구간_생성_후_검증(신분당선_id, 논현역_id, 신논현역_id, 10L);

            // when
            지하철구간_삭제(신분당선_id, 신사역_id);

            // then
            ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_조회(신분당선_id);
            assertThat(responseToStationNames(지하철노선_조회_응답)).doesNotContain("신사역");
        }

        /**
         * Given 지하철 구간이 주어지고 When 노선에 등록된 하행 종점역을 삭제하면 Then 지하철 구간이 삭제된다.
         */
        @DisplayName("노선에 등록된 하행 종점역을 삭제한다.")
        @Test
        void deleteSection2() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);
            지하철구간_생성_후_검증(신분당선_id, 논현역_id, 신논현역_id, 10L);

            // when
            지하철구간_삭제(신분당선_id, 신논현역_id);

            // then
            ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_조회(신분당선_id);
            assertThat(responseToStationNames(지하철노선_조회_응답)).doesNotContain("신논현역");
        }

        /**
         * Given 지하철 구간이 주어지고 When 노선에 등록된 중간역을 삭제하면 Then 지하철 구간이 삭제된다.
         */
        @DisplayName("노선에 등록된 중간역을 삭제한다.")
        @Test
        void deleteSection3() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);
            지하철구간_생성_후_검증(신분당선_id, 논현역_id, 신논현역_id, 10L);

            // when
            지하철구간_삭제(신분당선_id, 논현역_id);

            // then
            ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_조회(신분당선_id);
            assertThat(responseToStationNames(지하철노선_조회_응답)).doesNotContain("논현역");
        }

        /**
         * Given 지하철 구간이 주어지고 When 지하철 구간을 삭제할 때 상행 종점역과 하행 종점역만 있는 경우(구간이 1개) Then 예외가 발생한다.
         */
        @DisplayName("삭제할 구간이 1개인 경우 예외가 발생한다.")
        @Test
        void deleteSectionException2() {
            //given
            Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

            // when
            ExtractableResponse<Response> 지하철구간_삭제_응답 = 지하철구간_삭제(신분당선_id, 논현역_id);

            // then
            assertThat(지하철구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        }
    }
}
