package nextstep.subway.acceptance;

import nextstep.subway.utils.AbstractAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 경로 기능 인수 테스트")
public class PathAcceptanceTest extends AbstractAcceptanceTest {

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 출발역과 도착역을 정하고 경로를 찾는다.
     * Then: 찾은 경로의 출발역과 도착역은 입력한 출발역, 도착역과 일치한다.
     * Then: 찾은 경로는 가능한 경로 중에서 거리가 제일 짧다.
     */
    @Test
    void 최단_경로_조회() {
        //given

        //when

        //then
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 출발역과 도착역이 같은 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 출발역과_도착역이_같은_경우() {
        //given

        //when

        //then
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 존재하지 않는 출발역과 존재하는 도착역의 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 존재하지_않는_출발역인_경우() {
        //given

        //when

        //then
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 존재하는 출발역과 존재하지 않는 도착역의 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 존재하지_않는_도착역인_경우() {
        //given

        //when

        //then
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 연결되어 있지 않는 출발역과 도착역의 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 연결되어_있지_않는_출발역과_도착역인_경우() {
        //given

        //when

        //then
    }
}
