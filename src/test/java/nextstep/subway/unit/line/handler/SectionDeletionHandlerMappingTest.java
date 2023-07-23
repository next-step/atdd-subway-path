package nextstep.subway.unit.line.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionDeletionHandlerMappingTest {

    /**
     * Given
     * When 상행 종착역과 구간을 핸들러 매핑에 전달하면
     * Then 반환 받은 핸들러 어댑터는 DeleteSectionAtTopHandler이며
     * And 핸들러 어댑터는 상행 종착역과 구간으로 삭제 행위를 할 수 있음.
     */
    @DisplayName("상행 종착역이 속한 구간 제거하는 핸들러 반환")
    @Test
    void returnDeleteSectionAtTopHandler() {

    }

    /**
     * Given
     * When 하행 종착역과 구간을 핸들러 매핑에 전달하면
     * Then 반환 받은 핸들러 어댑터는 DeleteSectionAtLastHandler이며
     * And 핸들러 어댑터는 하행 종착역과 구간으로 삭제 행위를 할 수 있음.
     */
    @DisplayName("하행 종착역이 속한 구간 제거하는 핸들러 반환")
    @Test
    void returnDeleteSectionAtLastHandler() {

    }

    /**
     * Given
     * When 상행과 하행 종착역이 아닌 역(A)과 구간을 핸들러 매핑에 전달하면
     * Then 반환 받은 핸들러 어댑터는 DeleteSectionAtMiddleHandler이며
     * And 핸들러 어댑터는 A역과 구간으로 삭제 행위를 할 수 있음.
     */
    @DisplayName("상행 종착역과 하행종착역이 아닌 중간 구간 제거하는 핸들러 반환")
    @Test
    void returnDeleteSectionAtMiddleHandler() {

    }
}
