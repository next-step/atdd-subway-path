package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private static final int CREATE_LINE_DISTANCE = 10;
    private static final int ADD_SECTION_DISTANCE = 5;

    @DisplayName("구간 추가 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우")
    @Test
    void addSection_1() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, CREATE_LINE_DISTANCE);

        line.addSection(new Section(line, 서울역, 종각역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(서울역, 종각역, 시청역);
    }

    @DisplayName("구간 추가 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 하행역과 기존 구간의 하행역이 같은 경우")
    @Test
    void addSection_2() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, CREATE_LINE_DISTANCE);

        line.addSection(new Section(line, 종각역, 시청역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(서울역, 종각역, 시청역);
    }

    @DisplayName("구간 추가 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_3() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, CREATE_LINE_DISTANCE);

        line.addSection(new Section(line, 종각역, 서울역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(종각역, 서울역, 시청역);
    }

    @DisplayName("구간 추가 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_4() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, CREATE_LINE_DISTANCE);

        line.addSection(new Section(line, 시청역, 종각역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(서울역, 시청역, 종각역);
    }

    @DisplayName("구간 추가 - 예외 케이스 - 역 사이에 새로운 역을 등록하는 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSection_5() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, CREATE_LINE_DISTANCE);

        assertThatThrownBy(() -> line.addSection(new Section(line, 서울역, 종각역, CREATE_LINE_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 - 예외 케이스 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSection_6() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, CREATE_LINE_DISTANCE);

        assertThatThrownBy(() -> line.addSection(new Section(line, 서울역, 시청역, ADD_SECTION_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 - 예외 케이스 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_7() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, CREATE_LINE_DISTANCE);

        assertThatThrownBy(() -> line.addSection(new Section(line, 강남역, 역삼역, ADD_SECTION_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
