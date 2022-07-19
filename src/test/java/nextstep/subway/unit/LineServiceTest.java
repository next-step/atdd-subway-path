package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

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
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // line.getSections 메서드를 통해 검증
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {

    }


}
