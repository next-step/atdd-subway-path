package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private static final int CREATE_LINE_DISTANCE = 10;
    private static final int ADD_SECTION_DISTANCE = 5;

    private Station 서울역;
    private Station 시청역;
    private Station 종각역;
    private Line line;

    @BeforeEach
    void beforeEach() {
        서울역 = new Station("서울역");
        시청역 = new Station("시청역");
        종각역 = new Station("종각역");

        line = new Line("1호선", "남색");
        line.addSection(new Section(line, 서울역, 시청역, CREATE_LINE_DISTANCE));
    }

    @DisplayName("구간 추가 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우")
    @Test
    void addSection_1() {
        line.addSection(new Section(line, 서울역, 종각역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(서울역, 종각역, 시청역);
        assertThat(line.getDistances()).containsExactly(ADD_SECTION_DISTANCE, ADD_SECTION_DISTANCE);
    }

    @DisplayName("구간 추가 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 하행역과 기존 구간의 하행역이 같은 경우")
    @Test
    void addSection_2() {
        line.addSection(new Section(line, 종각역, 시청역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(서울역, 종각역, 시청역);
        assertThat(line.getDistances()).containsExactly(ADD_SECTION_DISTANCE, ADD_SECTION_DISTANCE);
    }

    @DisplayName("구간 추가 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_3() {
        line.addSection(new Section(line, 종각역, 서울역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(종각역, 서울역, 시청역);
        assertThat(line.getDistances()).containsExactly(ADD_SECTION_DISTANCE, CREATE_LINE_DISTANCE);
    }

    @DisplayName("구간 추가 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_4() {
        line.addSection(new Section(line, 시청역, 종각역, ADD_SECTION_DISTANCE));

        assertThat(line.getStations()).containsExactly(서울역, 시청역, 종각역);
        assertThat(line.getDistances()).containsExactly(CREATE_LINE_DISTANCE, ADD_SECTION_DISTANCE);
    }

    @DisplayName("구간 추가 - 예외 케이스 - 역 사이에 새로운 역을 등록하는 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSection_5() {
        assertThatThrownBy(() -> line.addSection(new Section(line, 서울역, 종각역, CREATE_LINE_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(line.getDistances()).containsExactly(CREATE_LINE_DISTANCE);
    }

    @DisplayName("구간 추가 - 예외 케이스 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSection_6() {
        assertThatThrownBy(() -> line.addSection(new Section(line, 서울역, 시청역, ADD_SECTION_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(line.getDistances()).containsExactly(CREATE_LINE_DISTANCE);
    }

    @DisplayName("구간 추가 - 예외 케이스  - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_7() {
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        assertThatThrownBy(() -> line.addSection(new Section(line, 강남역, 역삼역, ADD_SECTION_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(line.getDistances()).containsExactly(CREATE_LINE_DISTANCE);
    }

    @DisplayName("구간 삭제 - 종점이 제거될 경우 다음으로 오던 역이 종점이 됨 - 종점 하행역 제거")
    @Test
    void removeSection_1() {
        line.addSection(new Section(line, 시청역, 종각역, ADD_SECTION_DISTANCE));

        line.removeSection(종각역);

        assertThat(line.getStations()).containsExactly(서울역, 시청역);
        assertThat(line.getDistances()).containsExactly(CREATE_LINE_DISTANCE);
    }

    @DisplayName("구간 삭제 - 종점이 제거될 경우 다음으로 오던 역이 종점이 됨 - 종점 상행역 제거")
    @Test
    void removeSection_2() {
        line.addSection(new Section(line, 시청역, 종각역, ADD_SECTION_DISTANCE));

        line.removeSection(서울역);

        assertThat(line.getStations()).containsExactly(시청역, 종각역);
        assertThat(line.getDistances()).containsExactly(ADD_SECTION_DISTANCE);
    }

    @DisplayName("구간 삭제 - 중간역이 제거될 경우 재배치를 함")
    @Test
    void removeSection_3() {
        Station 종로3가 = new Station("종로3가");

        line.addSection(new Section(line, 시청역, 종각역, ADD_SECTION_DISTANCE));
        line.addSection(new Section(line, 종각역, 종로3가, 3));

        line.removeSection(시청역);
        line.removeSection(종각역);


        assertThat(line.getStations()).containsExactly(서울역, 종로3가);
        assertThat(line.getDistances()).containsExactly(18);
    }

    @DisplayName("구간 삭제 - 예외 케이스 - 구간이 하나인 노선에서 마지막 구간을 제거할 때")
    @Test
    void removeSection_4() {
        assertThatThrownBy(() -> line.removeSection(시청역))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(line.getStations()).containsExactly(서울역, 시청역);
        assertThat(line.getDistances()).containsExactly(CREATE_LINE_DISTANCE);
    }

    @DisplayName("구간 삭제 - 예외 케이스 - 노선에 등록되지 않은 역을 제거할 때")
    @Test
    void removeSection_5() {
        line.addSection(new Section(line, 시청역, 종각역, ADD_SECTION_DISTANCE));

        Station 강남역 = new Station("강남역");

        assertThatThrownBy(() -> line.removeSection(강남역))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(line.getStations()).containsExactly(서울역, 시청역, 종각역);
        assertThat(line.getDistances()).containsExactly(CREATE_LINE_DISTANCE, ADD_SECTION_DISTANCE);
    }
}
