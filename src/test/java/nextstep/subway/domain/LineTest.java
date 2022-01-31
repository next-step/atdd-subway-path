package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.exception.DuplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineTest {

    @DisplayName("노선 중간에 구간을 추가할 수 있다.")
    @Test
    void isNotDownStation() {
        Line line = new Line();
        Station 상행역 = new Station(1L, 기존지하철);
        Station 하행역 = new Station(2L, 새로운지하철);
        Station 중간역 = new Station(3L, "중간역");
        line.addSection(Section.of(상행역, 하행역, 역간_거리));

        line.addSection(Section.of(상행역, 중간역, 역간_거리 - 1));
        assertThat(line.getSectionSize()).isEqualTo(2);
    }

    @DisplayName("노선 리스폰스에 중복은 없다")
    @Test
    void 노선_리스폰스_중복_제거() {
        Line line = new Line();
        Station 상행역 = new Station(1L, 기존지하철);
        Station 하행역 = new Station(2L, 새로운지하철);
        Station 중간역 = new Station(3L, "중간역");
        line.addSection(Section.of(상행역, 하행역, 역간_거리));

        line.addSection(Section.of(상행역, 중간역, 역간_거리 - 1));

        LineResponse lineResponse = LineResponse.of(line);
        assertThat(lineResponse.getStations().size()).isEqualTo(3);
    }

    @DisplayName("이미 노선에 모두 등록된 역은 구간에 등록이 불가하다")
    @Test
    void 노선에_등록된_역들은_추가_등록이_불가하다() {
        Line line = new Line();
        Station 상행역 = new Station(1L, 기존지하철);
        Station 하행역 = new Station(2L, 새로운지하철);
        Station 중간역 = new Station(3L, "중간역");
        line.addSection(Section.of(상행역, 하행역, 역간_거리));
        line.addSection(Section.of(상행역, 중간역, 역간_거리 - 1));

        Assertions.assertThrows(DuplicationException.class, () ->
                line.addSection(Section.of(상행역, 하행역, 역간_거리 - 2))
        );

    }
}
