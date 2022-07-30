package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.fixture.LineFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SectionsTest {
    private static final Section 강남역_역삼역 = new Section(강남역, 역삼역, 10);
    private static final Section 잠실역_강남역 = new Section(잠실역, 강남역, 10);
    private static final Section 잠실역_판교역 = new Section(잠실역, 판교역, 10);
    private static final Section 역삼역_잠실역 = new Section(역삼역, 잠실역, 10);

    @Test
    @DisplayName("상행 구간을 추가할 수 있다.")
    void addUpSection() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);

        // when & then
        assertDoesNotThrow(
            () -> 노선_구간.addUpSection(잠실역_강남역)
        );
    }

    @Test
    @DisplayName("추가하는 상행선이 노선에 포함된 역이면 예외가 발생한다.")
    void addUpSectionValidation1() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.addUpSection(잠실역_강남역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가하는 구간의 하행역이 기존 상행 종점역과 같지 않으면 예외가 발생한다.")
    void addUpSectionValidation2() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.addUpSection(잠실역_판교역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("하행 구간을 추가할 수 있다.")
    void downSection() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);

        // when & then
        assertDoesNotThrow(
            () -> 노선_구간.addDownSection(역삼역_잠실역)
        );
    }

    @Test
    @DisplayName("추가하는 하행선이 노선에 포함된 역이면 예외가 발생한다.")
    void downSectionValidation1() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.addDownSection(잠실역_강남역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("마지막 구간의 하행선이 추가하는 구간의 상행선과 같지 않으면 예외가 발생한다.")
    void downSectionValidation2() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.addDownSection(잠실역_판교역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역이 동일한 중간 구간을 추가할 수 있다.")
    void addMiddleSection1() {
        // given
        Section 역삼역_판교역 = new Section(역삼역, 판교역, 3);
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addDownSection(역삼역_잠실역);
        int 기존_노선_개수 = 노선_구간.getSections().size();
        // when & then
        assertAll(
            () -> assertDoesNotThrow(() -> 노선_구간.addMiddleSection(역삼역_판교역)),
            () -> assertThat(노선_구간.getSections()).hasSize(기존_노선_개수 + 1)
        );
    }

    @Test
    @DisplayName("하행역이 동일한 중간 구간을 추가할 수 있다.")
    void addMiddleSection2() {
        // given
        Section 판교역_역삼역 = new Section(판교역, 역삼역, 3);
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addDownSection(역삼역_잠실역);

        int 기존_노선_개수 = 노선_구간.getSections().size();
        // when & then
        assertAll(
            () -> assertDoesNotThrow(() -> 노선_구간.addMiddleSection(판교역_역삼역)),
            () -> assertThat(노선_구간.getSections()).hasSize(기존_노선_개수 + 1)
        );
    }

    @Test
    @DisplayName("중간 구간 추가할 때, 추가하는 역이 노선에 포함된 역이면 예외가 발생한다.")
    void addMiddleSectionValidation1() {
        // given
        Section 역삼역_강남역 = new Section(역삼역, 강남역, 2);
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addDownSection(역삼역_잠실역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.addMiddleSection(역삼역_강남역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {15, 10})
    @DisplayName("역 사이에 새로운 역을 등록할 경우 등록하는 역 간의 길이가 기존 역 사이 길이보다 크거나 같으면 예외가 발생한다.")
    void addMiddleSectionValidation2(int distance) {
        // given
        Section 역삼역_판교역 = new Section(역삼역, 판교역, distance);
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addDownSection(역삼역_잠실역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.addMiddleSection(역삼역_판교역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가하려는 구간의 역 중 하나는 노선에 포함되지 않으면 예외가 발생한다.")
    void addMiddleSectionValidation3() {
        // given
        Section 신림역_판교역 = new Section(신림역, 판교역, 10);
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addDownSection(역삼역_잠실역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.addMiddleSection(신림역_판교역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행 종점역부터 하행 종점역까지 순서대로 반환받는다.")
    void getSections() {
        // given
        Section 판교역_역삼역 = new Section(판교역, 역삼역, 3);
        Section 신림역_판교역 = new Section(신림역, 판교역, 2);
        Sections 노선_구간 = new Sections(강남역_역삼역);
        // when
        노선_구간.addDownSection(역삼역_잠실역);
        노선_구간.addMiddleSection(판교역_역삼역);
        노선_구간.addMiddleSection(신림역_판교역);

        List<Station> stationList = 노선_구간.getSections().stream().map(Section::getUpStation).collect(Collectors.toList());
        stationList.add(노선_구간.getSections().get(노선_구간.getSections().size() - 1).getDownStation());

        // then
        assertThat(stationList).containsExactly(강남역, 신림역, 판교역, 역삼역, 잠실역);
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteSection() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);
        노선_구간.addSection(잠실역_판교역);
        int 노선_길이 = 노선_구간.getSections().size();

        // when & then
        assertAll(
            () -> assertDoesNotThrow(() -> 노선_구간.deleteSection(판교역)),
            () -> assertThat(노선_구간.getSections()).hasSize(노선_길이 - 1)
        );
    }

    @Test
    @DisplayName("삭제하려는 역이 노선에 없으면 안된다.")
    void deleteSectionValidation1() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.deleteSection(판교역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("현재 존재하는 노선이 하나이면 삭제할 수 없다.")
    void deleteSectionValidation2() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);

        // when & then
        assertThatThrownBy(
            () -> 노선_구간.deleteSection(강남역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행 종점역을 삭제하는 경우 상행 노선을 삭제한다.")
    void deleteSectionValidation3() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);
        노선_구간.addSection(잠실역_판교역);
        int 노선_길이 = 노선_구간.getSections().size();

        // when & then
        assertAll(
            () -> assertDoesNotThrow(() -> 노선_구간.deleteSection(강남역)),
            () -> assertThat(노선_구간.getSections()).hasSize(노선_길이 - 1)
        );
    }

    @Test
    @DisplayName("상행 종점역을 삭제하는 경우 다음으로 오는 역이 상행 종점역이 된다.")
    void deleteSectionValidation4() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);
        노선_구간.addSection(잠실역_판교역);

        // when
        노선_구간.deleteSection(강남역);

        // then
        Station 상행종점역 = 노선_구간.getSections().get(0).getUpStation();

        assertThat(상행종점역).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("중간역을 삭제하는 경우 중간 노선을 삭제한다.")
    void deleteSectionValidation5() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);
        노선_구간.addSection(잠실역_판교역);
        int 노선_길이 = 노선_구간.getSections().size();

        // when & then
        assertAll(
            () -> assertDoesNotThrow(() -> 노선_구간.deleteSection(역삼역)),
            () -> assertThat(노선_구간.getSections()).hasSize(노선_길이 - 1)
        );
    }

    @Test
    @DisplayName("중간역을 삭제하는 경우 기존에 있던 두 노선을 하나로 합친다.")
    void deleteSectionValidation6() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);
        노선_구간.addSection(잠실역_판교역);

        // when
        노선_구간.deleteSection(역삼역);

        // then
        Section 강남역_잠실역 = new Section(강남역, 잠실역, 20);

        assertAll(
            () -> assertThat(노선_구간.getSections()).contains(강남역_잠실역),
            () -> assertThat(노선_구간.getSections().get(0).getDistance()).isEqualTo(강남역_역삼역.getDistance() + 잠실역_판교역.getDistance())
        );
    }


    @Test
    @DisplayName("하행 종점역을 삭제하는 경우 하행 노선을 삭제한다.")
    void deleteSectionValidation7() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);
        노선_구간.addSection(잠실역_판교역);
        int 노선_길이 = 노선_구간.getSections().size();

        // when & then
        assertAll(
            () -> assertDoesNotThrow(() -> 노선_구간.deleteSection(판교역)),
            () -> assertThat(노선_구간.getSections()).hasSize(노선_길이 - 1)
        );
    }

    @Test
    @DisplayName(" 하행 노선을 삭제하는 경우 다음으로 오던 역이 종점이 된다.")
    void deleteSectionValidation8() {
        // given
        Sections 노선_구간 = new Sections(강남역_역삼역);
        노선_구간.addSection(역삼역_잠실역);
        노선_구간.addSection(잠실역_판교역);

        // when
        노선_구간.deleteSection(판교역);

        // then
        Section 역삼역_잠실역 = new Section(역삼역, 잠실역, 10);

        assertThat(노선_구간.getSections()).contains(역삼역_잠실역);
    }
}
