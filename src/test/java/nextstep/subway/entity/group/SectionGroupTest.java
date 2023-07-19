package nextstep.subway.entity.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.fixture.entity.StationFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;

class SectionGroupTest {

    private static final Line line = BDDMockito.mock(Line.class);
    private static final int distance = 10;
    private static final int FIRST = 0;
    private static final int SECOND = 1;

    private static final int THIRD = 2;

    private static SectionGroup group;

    @BeforeEach
    void init() {
        group = SectionGroup.of(
            List.of(
                new Section(line, StationFixture.of(1), StationFixture.of(3), distance, true,
                    false),
                new Section(line, StationFixture.of(3), StationFixture.of(5), distance, false,
                    false),
                new Section(line, StationFixture.of(5), StationFixture.of(7), distance, false, true)
            )
        );
    }

    @Test
    @DisplayName("상행 종점 구간 추가")
    void addUpEndStation() {

        //when
        Section section = group.add(line, StationFixture.of(0), StationFixture.of(1), distance);

        //then
        Assertions.assertAll(
            () -> assertThat(section.isUpEndPointSection()).isTrue(),
            () -> assertThat(section.isDownEndPointSection()).isFalse(),
            () -> assertThat(group.getSections().get(FIRST).isUpEndPointSection()).isFalse()
        );

    }

    @Test
    @DisplayName("상행종점역 구간에 상행역이 같은 중간 구간 추가")
    void addUpEndStationAtUpBound() {

        //when
        int interDistance = 3;
        Section section = group.add(line, StationFixture.of(1), StationFixture.of(2), interDistance);

        //then
        Assertions.assertAll(
            () -> assertThat(section.isUpEndPointSection()).isTrue(),
            () -> assertThat(section.isDownEndPointSection()).isFalse(),
            () -> assertThat(group.getSections().get(FIRST).isUpEndPointSection()).isFalse(),
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
        Section section = group.add(line, StationFixture.of(3), StationFixture.of(4), interDistance);

        //then
        Assertions.assertAll(
            () -> assertThat(section.isUpEndPointSection()).isFalse(),
            () -> assertThat(section.isDownEndPointSection()).isFalse(),
            () -> assertThat(group.getSections().get(SECOND).getUpStationId())
                .isEqualTo(section.getDownStationId()),
            () -> assertThat(group.getSections().get(SECOND).getDistance())
                .isEqualTo(distance - interDistance)
        );

    }

    @Test
    @DisplayName("하행역이 같은 중간 구간 추가")
    void addDownBound() {

        //when
        int interDistance = 6;
        Section section = group.add(line, StationFixture.of(2), StationFixture.of(3), interDistance);

        //then
        Assertions.assertAll(
            () -> assertThat(section.isUpEndPointSection()).isFalse(),
            () -> assertThat(section.isDownEndPointSection()).isFalse(),
            () -> assertThat(group.getSections().get(FIRST).getDownStationId())
                .isEqualTo(section.getUpStationId()),
            () -> assertThat(group.getSections().get(FIRST).getDistance())
                .isEqualTo(distance - interDistance)
        );

    }

    @Test
    @DisplayName("하행종점역 구간에 하행역이 같은 중간 구간 추가")
    void addDownEndStationAtDownBound() {

        //when
        int interDistance = 9;
        Section section = group.add(line, StationFixture.of(6), StationFixture.of(7), interDistance);

        //then
        Assertions.assertAll(
            () -> assertThat(section.isUpEndPointSection()).isFalse(),
            () -> assertThat(section.isDownEndPointSection()).isTrue(),
            () -> assertThat(group.getSections().get(THIRD).isDownEndPointSection()).isFalse(),
            () -> assertThat(group.getSections().get(THIRD).getDownStationId())
                .isEqualTo(section.getUpStationId()),
            () -> assertThat(group.getSections().get(THIRD).getDistance())
                .isEqualTo(distance - interDistance)
        );

    }

    @Test
    @DisplayName("하행 종점 구간 추가")
    void addDownEndStation() {

        //when
        Section section = group.add(line, StationFixture.of(7), StationFixture.of(8), distance);

        //then
        Assertions.assertAll(
            () -> assertThat(section.isUpEndPointSection()).isFalse(),
            () -> assertThat(section.isDownEndPointSection()).isTrue(),
            () -> assertThat(group.getSections().get(THIRD).isDownEndPointSection()).isFalse(),
            () -> assertThat(group.getSections().get(THIRD).getDownStationId())
                .isEqualTo(section.getUpStationId())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {distance, distance+1})
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void validAddSectionDistance(int addDistance) {

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

        //then
        assertThatThrownBy(
            () -> group.add(line, StationFixture.of(upStationId), StationFixture.of(downStationId), distance - 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");

    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void validNoneExistAddSectionStationAll() {

        //then
        assertThatThrownBy(
            () -> group.add(line, StationFixture.of(11), StationFixture.of(12), distance - 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("상행역과 하행역 둘 중 하나도 전체구간에 포함되지 않습니다.");

    }

}