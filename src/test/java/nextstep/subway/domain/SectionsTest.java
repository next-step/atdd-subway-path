package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.exception.CannotRegisterSectionException;
import nextstep.subway.exception.CannotRemoveSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Sections test")
class SectionsTest {

    /**
     * 강남역 --10-- 양재역 --5-- 판교역 --3-- 정자역
     */
    private Sections sections;
    Station 강남역 = new Station("강남역");
    Station 양재역 = new Station("양재역");
    Station 판교역 = new Station("판교역");
    Station 정자역 = new Station("정자역");

    @BeforeEach
    void setUp() {
        sections = new Sections();
        sections.addSection(강남역, 양재역, 10);
        sections.addSection(양재역, 판교역, 5);
        sections.addSection(판교역, 정자역, 3);
    }

    @Test
    @DisplayName("가장 앞에 구간 추가")
    void 가장_앞에_구간_추가() {
        Station 맨앞역 = new Station("맨앞역");
        sections.addSection(맨앞역, 강남역, 10);
        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(맨앞역, 강남역, 양재역, 판교역, 정자역);
    }

    @Test
    @DisplayName("가장 뒤에 구간 추가")
    void 가장_뒤에_구간_추가() {
        Station 맨앞역 = new Station("맨앞역");
        sections.addSection(정자역, 맨앞역, 10);
        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(강남역, 양재역, 판교역, 정자역, 맨앞역);
    }

    @Test
    @DisplayName("중간에 구간 추가(상행역이 존재하는 역)")
    void 중간에_구간_추가_상행역이_존재하는역() {
        Station 중간역 = new Station("중간역");
        sections.addSection(양재역, 중간역, 3);
        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(강남역, 양재역, 중간역, 판교역, 정자역);
    }

    @Test
    @DisplayName("중간에 구간 추가(하행역이 존재하는 역)")
    void 중간에_구간_추가_하행역이_존재하는역() {
        Station 중간역 = new Station("중간역");
        sections.addSection(중간역, 양재역, 3);
        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(강남역, 중간역, 양재역, 판교역, 정자역);
    }

    @Test
    @DisplayName("등록하려는 구간의 모든 역이 등록 되어 있다면 예외")
    void 등록하려는_구간의_모든역이_등록되어_있을떄() {
        assertThatThrownBy(() -> sections.addSection(강남역, 양재역, 3))
                .isInstanceOf(CannotRegisterSectionException.class);
    }

    @Test
    @DisplayName("등록하려는 구간의 모든 역이 등록 되어 있지 않다면 예외")
    void 모든_역이_등록_되어_있지_않을때() {
        Station 없는역1 = new Station("없는역1");
        Station 없는역2 = new Station("없는역2");
        assertThatThrownBy(() -> sections.addSection(없는역1, 없는역2, 3))
                .isInstanceOf(CannotRegisterSectionException.class);
    }

    @Test
    @DisplayName("등록하려는 구간의 거리가 등록할 구간의 거리보다 클때")
    void 등록하려는_구간의_거리가_등록할_구간의_거리값보다_큼() {
        Station 중간역 = new Station("중간역");
        assertThatThrownBy(() -> sections.addSection(중간역, 양재역, 20))
                .isInstanceOf(CannotRegisterSectionException.class);
    }

    @Test
    @DisplayName("등록하려는 구간의 거리가 등록할 구간의 거리와 같을때")
    void 등록하려는_구간의_거리가_등록할_구간의_거리값과_같음() {
        Station 중간역 = new Station("중간역");
        assertThatThrownBy(() -> sections.addSection(중간역, 양재역, 10))
                .isInstanceOf(CannotRegisterSectionException.class);
    }

    @Test
    @DisplayName("삭제하려는 역이 노선의 상행역 일 때")
    void 삭제하려는_역이_노선의_상행역() {
        sections.removeSection(강남역);
        assertThat(sections.getStations()).containsExactly(양재역, 판교역, 정자역);
    }

    /**
     * 강남역 --10-- 양재역 --5-- 판교역 --3-- 정자역
     */
    @Test
    @DisplayName("삭제하려는 역이 노선의 하행역 일 때")
    void 삭제하려는_역이_노선의_하행역() {
        sections.removeSection(정자역);
        assertThat(sections.getStations()).containsExactly(강남역, 양재역, 판교역);
    }

    @Test
    @DisplayName("삭제하려는 역이 중간에 위치 할 떄")
    void 삭제하려는_역이_노선의_중간에_위치() {
        sections.removeSection(양재역);
        assertThat(sections.getStations()).containsExactly(강남역, 판교역, 정자역);
    }

    @Test
    @DisplayName("구간이 1개 이하인 노선에 삭제 요청")
    void 구간이_1개_이하인_노선에_삭제() {

    }
}