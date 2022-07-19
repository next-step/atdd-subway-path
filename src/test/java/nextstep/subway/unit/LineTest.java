package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 신분당선 = new Line("신분당선", "yellow");
        Section 지하철구간 = new Section(신분당선, 강남역, 역삼역 , 10);

        //when
        신분당선.addSection(지하철구간);

        //then
        assertThat(신분당선.getStations()).containsExactly(강남역,역삼역);
    }

    @DisplayName("지하철노선의 지하철역 목록 가져오기")
    @Test
    void getStations() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 신분당선 = new Line("신분당선", "yellow");
        Section 지하철구간 = new Section(신분당선, 강남역, 역삼역 , 10);

        //when
        신분당선.addSection(지하철구간);

        //then
        assertThat(신분당선.getStations()).containsExactly(강남역,역삼역);
    }

    @DisplayName("지하철 노선 제거")
    @Test
    void removeSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 신분당선 = new Line("신분당선", "yellow");
        신분당선.addSection(new Section(신분당선, 강남역, 역삼역 , 10));

        //when
        신분당선.removeSection();

        //then
        assertThat(신분당선.isEmptySections()).isTrue();
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void changeNameAndColor(){
        //given
        Line 신분당선 = new Line("신분당선", "yellow");

        //when
        신분당선.changeNameAndColor("분당선","green");

        //then
        assertThat(신분당선.getName()).isEqualTo("분당선");
        assertThat(신분당선.getColor()).isEqualTo("green");
    }

    @DisplayName("지하철 구간 비었는지 확인")
    @Test
    void isEmptySections(){
        //given
        Line 신분당선 = new Line("신분당선", "yellow");

        //when
        boolean isEmpty = 신분당선.isEmptySections();

        //then
        assertThat(isEmpty).isTrue();
    }

    @DisplayName("지하철 노선 하행종점역과 같은지 확인")
    @Test
    void isEqualLastSectionDownStation(){
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 신분당선 = new Line("신분당선", "yellow");
        신분당선.addSection(new Section(신분당선, 강남역, 역삼역 , 10));

        //when
        boolean isEqualLastSectionDownStation = 신분당선.isEqualLastSectionDownStation(역삼역);

        //then
        assertThat(isEqualLastSectionDownStation).isTrue();
    }
}
