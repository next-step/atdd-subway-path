package nextstep.subway.unit;

import static nextstep.subway.utils.EntityCreator.*;
import static org.assertj.core.api.Assertions.*;

import nextstep.subway.exception.AllIncludedStationException;
import nextstep.subway.exception.DistanceException;
import nextstep.subway.exception.MininumSectionException;
import nextstep.subway.exception.NonIncludedStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;

class LineTest {

    Line 신분당선;
    Station 양재역, 정자역;
    Section 양재_정자;
    int 양재_정자_거리 = 10;

    @BeforeEach
    void setUp() {
        // given
        신분당선 = createLine("신분당선", "bg-red-600");
        양재역 = createStation("양재역");
        정자역 = createStation("정자역");
        양재_정자= createSection(신분당선, 양재역, 정자역, 양재_정자_거리);

        신분당선.addSection(양재_정자);
    }

    @Test
    void 구간_추가_성공() {
        // then
        assertThat(신분당선.getSections()).contains(양재_정자);
    }

    @Test
    void 역_사이에_새로운_역을_등록() {
        // given
        Station 판교역 = createStation("판교역");
        Section 양재_판교 = createSection(신분당선, 양재역, 판교역, 7);

        // when
        신분당선.addSection(양재_판교);

        // then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getStations()).hasSize(3)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 양재역, 판교역));
    }

    @Test
    void 새로운_역을_상행_종점으로_등록() {
        // given
        Station 강남역 = createStation("강남역");
        Section 강남_양재 = createSection(신분당선, 강남역, 양재역, 7);

        // when
        신분당선.addSection(강남_양재);

        // then
        assertThat(신분당선.getSections()).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(양재_정자, 강남_양재));
        assertThat(신분당선.getStations()).hasSize(3)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 양재역, 강남역));
    }

    @Test
    void 새로운_역을_하행_종점으로_등록() {
        // given
        Station 미금역 = createStation("미금역");
        Section 정자_미금 = createSection(신분당선, 정자역, 미금역, 7);

        // when
        신분당선.addSection(정자_미금);

        // then
        assertThat(신분당선.getSections()).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(양재_정자, 정자_미금));
        assertThat(신분당선.getStations()).hasSize(3)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 양재역, 미금역));
    }

    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        // given
        Station 판교역 = createStation("판교역");
        Section 양재_판교 = createSection(신분당선, 양재역, 판교역, 양재_정자_거리);

        // when
        assertThatThrownBy(() -> 신분당선.addSection(양재_판교))
                .isInstanceOf(DistanceException.class);

        // then
        assertThat(신분당선.getSections()).hasSize(1)
                .containsExactly(양재_정자);
        assertThat(신분당선.getStations()).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 양재역));
    }


    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        // given
        Section new_양재_정자 = createSection(신분당선, 양재역, 정자역, 양재_정자_거리);

        // when
        assertThatThrownBy(() -> 신분당선.addSection(new_양재_정자))
                .isInstanceOf(AllIncludedStationException.class);

        // then
        assertThat(신분당선.getSections()).hasSize(1)
                .containsExactly(양재_정자);
        assertThat(신분당선.getStations()).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 양재역));
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어_있지_않으면_추가할_수_없음() {
        // given
        Station 상현역 = createStation("상현역");
        Station 광교역 = createStation("광교역");
        Section 상현_광교 = createSection(신분당선, 광교역, 상현역, 12);

        // when
        assertThatThrownBy(() -> 신분당선.addSection(상현_광교))
                .isInstanceOf(NonIncludedStationException.class);

        // then
        assertThat(신분당선.getSections()).hasSize(1)
                .containsExactly(양재_정자);
        assertThat(신분당선.getStations()).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 양재역));
    }

    @Test
    void 모든_역_가져오기() {
        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 양재역));
    }

    @Test
    void 지하철_노선의_하행_종점_구간_제거() {
        // given
        Station 미금역 = createStation("미금역");
        Section 정자_미금 = createSection(신분당선, 정자역, 미금역, 10);
        신분당선.addSection(정자_미금);

        // when
        신분당선.deleteSection(미금역);

        // then
        assertThat(신분당선.getSections()).hasSize(1).doesNotContain(정자_미금);
        assertThat(신분당선.getStations()).hasSize(2).doesNotContain(미금역);
    }

    @Test
    void 지하철_노선의_중간_구간_제거() {
        // given
        int 정자_미금_거리 = 10;
        Station 미금역 = createStation("미금역");
        Section 정자_미금 = createSection(신분당선, 정자역, 미금역, 정자_미금_거리);
        신분당선.addSection(정자_미금);

        // when
        신분당선.deleteSection(정자역);

        // then
        assertThat(신분당선.getSections()).hasSize(1);
        assertThat(신분당선.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(신분당선.getSections().get(0).getDownStation()).isEqualTo(미금역);
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(양재_정자_거리 + 정자_미금_거리);
        assertThat(신분당선.getStations()).hasSize(2).doesNotContain(정자역);
    }

    @Test
    void 구간이_하나인_노선에서_마지막_구간을_제거() {
        // when
        assertThatThrownBy(() -> 신분당선.deleteSection(정자역))
                .isInstanceOf(MininumSectionException.class);

        // then
        assertThat(신분당선.getSections()).hasSize(1).containsExactly(양재_정자);
        assertThat(신분당선.getStations()).hasSize(2).containsExactly(양재역, 정자역);
    }

    @Test
    void 지하철_노선에_없는_구간_제거() {
        // given
        Station 미금역 = createStation("미금역");
        Section 정자_미금 = createSection(신분당선, 정자역, 미금역, 10);
        신분당선.addSection(정자_미금);

        Station 동천역 = createStation("동천역");
        Section 미금_동천 = createSection(신분당선, 미금역, 동천역, 10);
        신분당선.addSection(미금_동천);

        Station 성복역 = createStation("성복역");

        // when
        assertThatThrownBy(() -> 신분당선.deleteSection(성복역))
                .isInstanceOf(NonIncludedStationException.class);

        // then
        assertThat(신분당선.getSections()).hasSize(3).containsExactly(양재_정자, 정자_미금, 미금_동천);
        assertThat(신분당선.getStations()).hasSize(4).containsExactly(양재역, 정자역, 미금역, 동천역);
    }
}
