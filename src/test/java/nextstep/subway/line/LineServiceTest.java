package nextstep.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    /**
     * Input = LineRequest
     * Output = LineResponse
     *
     * given DB에 노선 정보 저장 시, 성공적으로 저장된다.
     * given 노선에 포함된 역 목록을 DB에서 조회시, 역 리스트(역 두개)를 반환한다.
     * When LineService.saveLine() 호출 시
     * Then LineResponse를 반환한다.
     */
    @DisplayName("노선을 생성한다")
    @Test
    void saveLine() {
        // 2주차에 구현 예정
    }
}