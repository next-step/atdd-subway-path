package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.ExceptionCode;

class LineTest {
    private Line 이호선;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    void init() {
        // given
        이호선 = new Line("2호선", "bg-green-600");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");

        이호선.addSection(교대역, 역삼역, 15);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void 구간_목록_마지막에_새로운_구간_등록() {
        // when
        이호선.addSection(역삼역, 선릉역, 15);

        // then
        List<Section> sections = 이호선.getSections();
        Section section = sections.stream()
            .filter(it -> it.getUpStation().equals(역삼역))
            .findFirst()
            .orElseThrow(RuntimeException::new);

        assertAll(
            () -> assertThat(section.getUpStation().equals(역삼역)).isTrue(),
            () -> assertThat(section.getDownStation().equals(선릉역)).isTrue(),
            () -> assertThat(section.getDistance()).isEqualTo(15)
        );
    }

    @DisplayName("구간 목록 사이에 상행 기준으로 새로운 구간을 추가할 경우")
    @Test
    void 구간_목록_사이에_상행_기준으로_새로운_구간_등록() {
        // when
        이호선.addSection(교대역, 강남역, 5);

        // then
        List<Section> sections = 이호선.getSections();
        Section section = sections.stream()
            .filter(it -> it.getUpStation().equals(교대역))
            .findFirst()
            .orElseThrow(RuntimeException::new);

        assertAll(
            () -> assertThat(section.getUpStation().equals(교대역)).isTrue(),
            () -> assertThat(section.getDownStation().equals(강남역)).isTrue(),
            () -> assertThat(section.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("구간 목록 사이에 하행 기준으로 새로운 구간을 추가할 경우")
    @Test
    void 구간_목록_사이에_하행_기준으로_새로운_구간_등록() {
        // when
        이호선.addSection(강남역, 역삼역, 3);

        // then
        List<Section> sections = 이호선.getSections();
        Section section = sections.stream()
            .filter(it -> it.getUpStation().equals(강남역))
            .findFirst()
            .orElseThrow(RuntimeException::new);

        assertAll(
            () -> assertThat(section.getUpStation().equals(강남역)).isTrue(),
            () -> assertThat(section.getDownStation().equals(역삼역)).isTrue(),
            () -> assertThat(section.getDistance()).isEqualTo(3)
        );
    }

    @DisplayName("등록할 구간이 이미 등록 되어 있는 경우")
    @Test
    void 등록할_구간이_이미_존재할_경우_예외() {
        // when then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> 이호선.addSection(역삼역, 교대역, 3))
            .withMessageContaining(ExceptionCode.DUPLICATE_SECTION.getMessage());
    }

    @DisplayName("등록할 구간의 상행역과 하행역 중 하나가 기존 구간에 포함되어 있지 않는 경우")
    @Test
    void 등록할_구간의_상행역이나_하행역이_기존_구간에_포함되어_있지_않는_경우_예외() {
        // when then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> 이호선.addSection(강남역, 선릉역, 3))
            .withMessageContaining(ExceptionCode.DO_NOT_ADD_SECTION.getMessage());
    }

    @DisplayName("구간 사이에 새로운 구간을 등록할 때 기존 구간보다 새로운 구간의 거리가 크거나 같을 경우")
    @ParameterizedTest
    @ValueSource(ints = {20, 25})
    void 기존_구간_사이에_등록할_구간의_거리가_크거나_같을_경우_예외(int distance) {
        // when then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> 이호선.addSection(교대역, 강남역, distance))
            .withMessageContaining(ExceptionCode.DO_NOT_ADD_SECTION.getMessage());
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
    }

    @DisplayName("지하철 구간 목록에서 마지막 역 삭제")
    @Test
    void 마지막_역_삭제() {
        // given
        이호선.addSection(역삼역, 선릉역, 15);
        // when
        이호선.deleteSection(선릉역);

        // then
        List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(교대역, 역삼역);
    }

    @DisplayName("지하철 구간 목록에서 맨 앞 역 삭제")
    @Test
    void 지하철_구간_맨_앞_역_삭제() {
        // given
        이호선.addSection(역삼역, 선릉역, 15);
        // when
        이호선.deleteSection(교대역);

        // then
        List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(역삼역, 선릉역);
    }

    @DisplayName("지하철 구간 목록에서 중간역 삭제")
    @Test
    void 지하철_구간_중간역_삭제() {
        // given
        이호선.addSection(역삼역, 선릉역, 15);
        // when
        이호선.deleteSection(역삼역);

        // then
        List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(교대역, 선릉역);
    }

    @DisplayName("구간이 목록에 없는 역 삭제")
    @Test
    void 지하철_구간_안에_없는_역_삭제() {
        // given
        이호선.addSection(역삼역, 선릉역, 15);

        // when then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> 이호선.deleteSection(강남역));
    }

    @DisplayName("지하철 하나 남은 구간의 역 삭제")
    @Test
    void 지하철_하나_남은_구간의_역_삭제() {
        // when then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> 이호선.deleteSection(교대역));
    }
}
