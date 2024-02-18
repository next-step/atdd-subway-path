package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.station.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import nextstep.subway.E2ETestInitializer;
import nextstep.subway.utils.line.StationLineManager;
import nextstep.subway.utils.section.StationSectionManager;
import nextstep.subway.utils.station.StationManager;

@DisplayName("지하철역 구간 관리 기능")
public class SectionAcceptanceTest extends E2ETestInitializer {

    public static final long 거리 = 10L;
    public static final String _2호선_NAME = "_2호선";
    public static final String _2호선_COLOR = "bg-red-600";

    private static LineResponse 라인_응답_DTO;
    private static LineRequest 라인_요청_DTO;

    private static long 강남역_ID;
    private static long 역삼역_ID;
    private static long 선릉역_ID;

    @BeforeEach
    void setUp() {
        강남역_ID = StationManager.save("강남역").jsonPath().getLong("id");
        역삼역_ID = StationManager.save("역삼역").jsonPath().getLong("id");
        선릉역_ID = StationManager.save("선릉역").jsonPath().getLong("id");

        라인_요청_DTO = new LineRequest(_2호선_NAME, _2호선_COLOR, 강남역_ID, 역삼역_ID, 거리);
        라인_응답_DTO = StationLineManager.save(라인_요청_DTO).as(LineResponse.class);  // A - C
    }

    /**
     * Given 지하철 노선을 생성하고 여기에 A역과 C역을 생성한다.
     * When B역을 등록한다.
     *   1. 노선 처음에 추가 할 수 있다.
     *   2. 노선 가운데 추가 할 수 있다.
     *   3. 노선 마지막에 추가 할 수 있다.
     * Then 노선을 조회했을 때 역이 3개가 등록되어 있다.
     */
    // TODO: 지하철 구간 등록 인수 테스트 메서드 생성
    @DisplayName("지하철 구간을 등록한다.")
    @ParameterizedTest
    @MethodSource("createSectionParameters")
    void createSection(SectionRequest requestDto) {
        // when
        ExtractableResponse<Response> response = StationSectionManager.save(라인_응답_DTO.getId(), requestDto); // 노선에 구간 추가

        // then
        List<StationResponse> result = response.jsonPath().getList("stations");
        Assertions.assertThat(result.size()).isEqualTo(3);
    }

    /**
     * Exception
     * 1. 등록하려는 역이 이미 등록되어 있는 경우
     * 2. 등록하려는 역의 상행역이 현재 노선의 하행역이 아닌 경우
     */
    @DisplayName("잘못된 구간을 등록하면 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("invalidSaveStationSectionParameters")
    void invalidCreateStationSection(SectionRequest requestDto) {
        // given
        // 현재 노선: A(1) - C(2)

        // when
        StationSectionManager.saveFailure(라인_응답_DTO.getId(), requestDto);
    }

    /**
     * Given 지하철 노선을 생성하고 A역 - C역 - B역 구간을 등록한다.
     * When B역을 제거한다.
     * Then A역 - C역 순으로 구간이 등록되어 있다.
     */
    // TODO: 지하철 구간 제거 인수 테스트 메서드 생성
    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteStationSection() {
        // when
        SectionRequest requestDto = new SectionRequest(역삼역_ID, 선릉역_ID, 거리); // C - B 구간
        StationSectionManager.save(라인_응답_DTO.getId(), requestDto); // 노선에 구간 추가

        // 구간 제거
        StationSectionManager.remove(라인_응답_DTO.getId(), 선릉역_ID);

        // 조회
        ExtractableResponse<Response> response = StationLineManager.findById(라인_응답_DTO.getId());

        // then
        List<StationResponse> result = response.jsonPath().getList("stations");
        Assertions.assertThat(result.size()).isEqualTo(2); // A - C
    }

    /**
     * Exception
     * 1. 제거하려는 역이 마지막 구간의 하행 종점역이 아닐 경우
     * 2. 구간이 1개인 경우
     */
    // TODO: 지하철 구간 제거 예외 테스트 메서드 생성
    @DisplayName("잘못된 구간을 제거하면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void invalidDeleteStationSection(long stationId) {
        // given
        // 현재 노선: A(1) - C(2)

        // when
        StationSectionManager.removeFailure(라인_응답_DTO.getId(), stationId);
    }

    public static Stream<Arguments> createSectionParameters() {
        return Stream.of( // setup() 함수가 실행되기 전에 실행되어서 StationId 값들이 0으로 채워져서 변수를 할당하지 못하네요..
                Arguments.of(new SectionRequest(3, 1, 거리)),  // B - A - C
                Arguments.of(new SectionRequest(1, 3, 거리)),  // A - B - C  (A - B) - (B - C)
                Arguments.of(new SectionRequest(2, 3, 거리))  // A - C - B
        );
    }

    private static Stream<Arguments> invalidSaveStationSectionParameters() {
        return Stream.of(
                Arguments.of(new SectionRequest(역삼역_ID, 강남역_ID, 거리)), // (A - C) - (C - A) // 이미 등록된 역(A)
                Arguments.of(new SectionRequest(강남역_ID, 역삼역_ID, 거리)) // (A - C) - (A - C) // 이미 등록된 역
        );
    }
}
