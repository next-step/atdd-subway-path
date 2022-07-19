package nextstep.subway.unit;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    @DisplayName("역 목록을 보여준다.")
    void showLines() {

    }

    @Test
    @DisplayName("식별자를 이용해 노선을 조회한다.")
    void findById() {

    }

    @Test
    @DisplayName("식별자로 노선을 조회하지 못하는 경우 예외가 발생한다.")
    void findByIdValidation1() {

    }

    @Test
    @DisplayName("노선의 이름과 색을 수정한다.")
    void updateLine() {

    }

    @Test
    @DisplayName("노선의 이름과 색을 수정할 때, 식별자로 노선을 조회하지 못하는 경우 예외가 발생한다.")
    void updateLineValidation1() {

    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() {

    }

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // line.findLineById 메서드를 통해 검증
    }


    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {

    }


}
