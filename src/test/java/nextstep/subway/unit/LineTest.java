package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    Line 신분당선;
    Station 신논현역;
    Station 논현역;
    Station 강남역;
    Station 양재역;
    Station 양재시민의숲;

    @BeforeEach
    void setUp() {
        신분당선 = Line.of("신분당선", "RED");
        신논현역 = new Station("신논현역");
        논현역 = new Station("논현역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        양재시민의숲 = new Station("양재시민의숲");
    }

    @DisplayName("구간 등록 성공")
    @Test
    void addSection() {
        신분당선.addSection(논현역, 신논현역, 5);
        assertThat(신분당선.sections().size()).isEqualTo(1);
        assertThat(신분당선.upStation().getName()).isEqualTo("논현역");
        assertThat(신분당선.downStation().getName()).isEqualTo("신논현역");
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        신분당선.addSection(논현역, 신논현역, 5);
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(강남역, 양재역, 5);
        assertThat(신분당선.stations()).containsExactly(논현역, 신논현역, 강남역, 양재역);
    }

    @DisplayName("구간 사이에 구간 추가")
    @Test
    void addSection2() {
        신분당선.addSection(논현역, 강남역, 5);
        신분당선.addSection(논현역, 신논현역, 3);
        assertThat(신분당선.stations()).containsExactly(논현역, 신논현역, 강남역);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addUpStation() {
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(논현역, 신논현역, 3);
        assertThat(신분당선.stations()).containsExactly(논현역, 신논현역, 강남역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addDownStation() {
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(강남역, 양재역, 3);
        assertThat(신분당선.stations()).containsExactly(신논현역, 강남역, 양재역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void 지하철역_등록_길이_오류() {
        신분당선.addSection(논현역, 강남역, 5);
        assertThatThrownBy(() -> {
            신분당선.addSection(논현역, 신논현역, 7);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("추가하려는 구간의 길이가 기존 길이보다 같거나 길 수 없습니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 상행역_하행역_중복_등록_불가2() {
        신분당선.addSection(신논현역, 강남역, 5);
        assertThatThrownBy(() -> {
            신분당선.addSection(논현역, 양재시민의숲, 5);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("최소 1개 이상의 역은 노선에 등록되어 있어야 합니다.");
    }

//    A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음1() {
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(강남역, 양재역, 5);
        assertThatThrownBy(() -> {
            신분당선.addSection(강남역, 양재역, 5);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("최대 1개의 역만 노선에 등록되어 있어야 합니다.");
    }

    //    A-B, B-C 구간이 등록된 상황에서 A-C 구간을 등록할 수 없음
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음2() {
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(강남역, 양재역, 5);
        assertThatThrownBy(() -> {
            신분당선.addSection(신논현역, 양재역, 2);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("최대 1개의 역만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("상행역 삭제")
    @Test
    void 상행역_삭제_테스트() {
        신분당선.addSection(논현역, 신논현역, 5);
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.deleteSection(논현역);
        assertThat(신분당선.stations()).containsExactly(신논현역, 강남역);
    }

    @DisplayName("하행역 삭제")
    @Test
    void removeSection() {
        신분당선.addSection(논현역, 신논현역, 5);
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.deleteSection(강남역);
        assertThat(신분당선.stations()).containsExactly(논현역, 신논현역);
    }

    @DisplayName("중간역 삭제")
    @Test
    void deleteMiddleSection() {
        신분당선.addSection(논현역, 신논현역, 5);
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.deleteSection(신논현역);
        assertThat(신분당선.stations()).containsExactly(논현역, 강남역);
    }

    @DisplayName("구간이 하나인 노선을 삭제할 때 삭제할 수 없음")
    @Test
    void deleteOneSection() {
        신분당선.addSection(논현역, 신논현역, 5);
        assertThatThrownBy(() -> {
            신분당선.deleteSection(신논현역);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("두 개 이상의 구간일때만 삭제가 가능합니다.");
    }
}
