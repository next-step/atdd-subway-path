package nextstep.subway.unit.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.group.SectionGroup;
import nextstep.subway.fixture.unit.entity.StationFixture;
import nextstep.subway.fixture.unit.group.SectionGroupFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;

class SectionGroupTest {

    private static final Line line = BDDMockito.mock(Line.class);
    private static final int distance = 10;
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;

    @Test
    @DisplayName("상행 종점 구간 추가")
    void addUpEndStation() {

        //given
        SectionGroup group = SectionGroupFixture.make();

        //when
        Section section = group.add(line, StationFixture.of(0), StationFixture.of(1), distance);

        //then
        SectionGroup newGroup = SectionGroupFixture.make(group.getSections(), section);

        Assertions.assertAll(
            () -> assertThat(newGroup.isEndUpSection(section)).isTrue(),
            () -> assertThat(newGroup.isEndDownSection(section)).isFalse()
        );
    }

    @Test
    @DisplayName("상행종점역 구간에 상행역이 같은 중간 구간 추가")
    void addUpEndStationAtUpBound() {

        //given
        int interDistance = 3;
        SectionGroup group = SectionGroupFixture.make();

        //when
        Section section = group.add(line, StationFixture.of(1), StationFixture.of(2), interDistance);

        //then
        SectionGroup newGroup = SectionGroupFixture.make(group.getSections(), section);

        //then
        Assertions.assertAll(
            () -> assertThat(newGroup.isEndUpSection(section)).isTrue(),
            () -> assertThat(newGroup.isEndDownSection(section)).isFalse(),
            () -> assertThat(group.getSections().get(FIRST).getUpStationId())
                .isEqualTo(section.getDownStationId()),
            () -> assertThat(group.getSections().get(FIRST).getDistance())
                .isEqualTo(distance - interDistance)
        );

    }

    @Test
    @DisplayName("상행역이 같은 중간 구간 추가")
    void addUpBound() {

        //when
        int interDistance = 6;
        SectionGroup group = SectionGroupFixture.make();

        //when
        Section section = group.add(line, StationFixture.of(3), StationFixture.of(4), interDistance);

        //then
        SectionGroup newGroup = SectionGroupFixture.make(group.getSections(), section);

        //then
        Assertions.assertAll(
            () -> assertThat(newGroup.isEndUpSection(section)).isFalse(),
            () -> assertThat(newGroup.isEndDownSection(section)).isFalse(),
            () -> assertThat(group.getSections().get(SECOND).getUpStationId())
                .isEqualTo(section.getDownStationId()),
            () -> assertThat(group.getSections().get(SECOND).getDistance())
                .isEqualTo(distance - interDistance)
        );

    }

    @Test
    @DisplayName("하행역이 같은 중간 구간 추가")
    void addDownBound() {

        //given
        int interDistance = 6;
        SectionGroup group = SectionGroupFixture.make();

        //when
        Section section = group.add(line, StationFixture.of(2), StationFixture.of(3), interDistance);

        //then
        SectionGroup newGroup = SectionGroupFixture.make(group.getSections(), section);

        //then
        Assertions.assertAll(
            () -> assertThat(newGroup.isEndUpSection(section)).isFalse(),
            () -> assertThat(newGroup.isEndDownSection(section)).isFalse(),
            () -> assertThat(group.getSections().get(FIRST).getDownStationId())
                .isEqualTo(section.getUpStationId()),
            () -> assertThat(group.getSections().get(FIRST).getDistance())
                .isEqualTo(distance - interDistance)
        );

    }

    @Test
    @DisplayName("하행종점역 구간에 하행역이 같은 중간 구간 추가")
    void addDownEndStationAtDownBound() {

        //given
        int interDistance = 9;
        SectionGroup group = SectionGroupFixture.make();

        //when
        Section section = group.add(line, StationFixture.of(6), StationFixture.of(7), interDistance);

        //then
        SectionGroup newGroup = SectionGroupFixture.make(group.getSections(), section);

        //then
        Assertions.assertAll(
            () -> assertThat(newGroup.isEndUpSection(section)).isFalse(),
            () -> assertThat(newGroup.isEndDownSection(section)).isTrue(),
            () -> assertThat(newGroup.isEndDownSection(newGroup.getSections().get(THIRD))).isFalse(),
            () -> assertThat(group.getSections().get(THIRD).getDownStationId())
                .isEqualTo(section.getUpStationId()),
            () -> assertThat(group.getSections().get(THIRD).getDistance())
                .isEqualTo(distance - interDistance)
        );

    }

    @Test
    @DisplayName("하행 종점 구간 추가")
    void addDownEndStation() {

        SectionGroup group = SectionGroupFixture.make();

        //when
        Section section = group.add(line, StationFixture.of(7), StationFixture.of(9), distance);

        //then
        SectionGroup newGroup = SectionGroupFixture.make(group.getSections(), section);

        //then
        Assertions.assertAll(
            () -> assertThat(newGroup.isEndUpSection(section)).isFalse(),
            () -> assertThat(newGroup.isEndDownSection(section)).isTrue()
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {distance, distance + 1})
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void validAddSectionDistance(int addDistance) {

        SectionGroup group = SectionGroupFixture.make();

        //then
        assertThatThrownBy(
            () -> group.add(line, StationFixture.of(1), StationFixture.of(2), addDistance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("추가할려는 구간이 기존 구간 길이와 같거나 더 깁니다.");

    }


    @ParameterizedTest
    @CsvSource(value = {"1,3", "1,5", "1,7", "3,3", "7,1", "7,5"})
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void validAlreadyExistAddSectionStationAll(int upStationId, int downStationId) {

        SectionGroup group = SectionGroupFixture.make();

        //then
        assertThatThrownBy(
            () -> group.add(line, StationFixture.of(upStationId), StationFixture.of(downStationId),
                distance - 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");

    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void validNoneExistAddSectionStationAll() {

        SectionGroup group = SectionGroupFixture.make();

        //then
        assertThatThrownBy(
            () -> group.add(line, StationFixture.of(11), StationFixture.of(12), distance - 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("상행역과 하행역 둘 중 하나도 전체구간에 포함되지 않습니다.");

    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("상행 종점 구간 비교 테스트")
    void checkIsEndUpSection(Section section, boolean result) {

        SectionGroup group = SectionGroupFixture.make();

        assertThat(group.isEndUpSection(section)).isEqualTo(result);
    }

    static Stream<Arguments> checkIsEndUpSection() {

        SectionGroup group = SectionGroupFixture.make();

        return Stream.of(
            Arguments.of(group.getSections().get(FIRST), true),
            Arguments.of(group.getSections().get(SECOND), false),
            Arguments.of(group.getSections().get(THIRD), false)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("하행 종점 구간 비교 테스트")
    void checkIsEndDownSection(Section section, boolean result) {

        SectionGroup group = SectionGroupFixture.make();

        assertThat(group.isEndDownSection(section)).isEqualTo(result);
    }

    static Stream<Arguments> checkIsEndDownSection() {

        SectionGroup group = SectionGroupFixture.make();

        return Stream.of(
            Arguments.of(group.getSections().get(FIRST), false),
            Arguments.of(group.getSections().get(SECOND), false),
            Arguments.of(group.getSections().get(THIRD), true)
        );
    }

}