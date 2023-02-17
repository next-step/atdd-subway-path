package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class LineTest {

    private Line 일호선;
    private Station 서울역;
    private Station 용산역;

    @BeforeEach
    void setUp() {
        일호선 = new Line("1호선", "bg-color-blue");
        서울역 = new Station("서울역");
        용산역 = new Station("용산역");
    }

    /**
     * given 생성된 노선과 지하철 역을 구간을 추가하고
     * when 생성된 노선에 구간을 추가하면
     * then 구간에 노선의 정보가 포함된다.
     */
    @Test
    void addSection() {
        //given
        Section 일구간 = new Section(일호선, 서울역, 용산역, 5);

        //when
        일호선.addSection(서울역, 용산역, 5);

        //then
        Assertions.assertThat(일호선.getSections().getSections().contains(일구간)).isTrue();
    }

    /**
     * given 생성된 노선에 구간을 추가하고
     * when 노선을 조회하면
     * then 구간에 포함된 지하철 역을 확인할 수 있다.
     */
    @Test
    void getStations() {
        //given
        Section 일구간 = new Section(일호선, 서울역, 용산역, 5);
        일호선.addSection(서울역, 용산역, 5);

        //when
        List<Station> stations = 일호선.getStations();

        //then
        Assertions.assertThat(stations).containsExactly(서울역, 용산역);
    }

    /**
     * given 생성된 노선에 구간을 추가하고
     * when 노선에 포함된 구간을 삭제하면
     * then 노선에 포함된 구간은 존재하지 않는다.
     */
    @Test
    void removeSection() {
        //given
        Section 일구간 = new Section(일호선, 서울역, 용산역, 5);
        일호선.addSection(서울역, 용산역, 5);

        //when
        일호선.removeSection(용산역);

        //then
        Assertions.assertThat(일호선.getStations()).isEmpty();
    }
}
