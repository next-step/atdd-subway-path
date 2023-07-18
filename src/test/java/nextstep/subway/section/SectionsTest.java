package nextstep.subway.section;

import static common.Constants.새로운지하철역;
import static common.Constants.신논현역;
import static common.Constants.지하철역;
import static nextstep.subway.section.SectionBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.station.Station;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("구간 관련")
public class SectionsTest {

    @DisplayName("마지막 순서의 구간을 만든다")
    @Test
    void createLastSection() {
        Section section = aSection().build();
        Sections sections = new Sections(new ArrayList<>(List.of(section)));
        sections.add(new Section(section.getLine(), new Station(2L, 신논현역), new Station(3L, 지하철역), 10));
        Section newSection = sections.getLastSection();

        assertAll(
            () -> assertThat(newSection.getUpStation().getId()).isEqualTo(2L),
            () -> assertThat(newSection.getDownStation().getId()).isEqualTo(3L)
        );
    }

    @DisplayName("구간 추가시 비즈니스 검증에 문제가 없다면 아무 일도 일어나지 않는다")
    @Test
    void validateSection() {
        // given
        Sections sections = new Sections(List.of(aSection().build()));

        // when & then
        sections.validate(new Station(2L, 신논현역), new Station(3L, 지하철역));
    }

    @DisplayName("구간 추가시 이미 등록된 역을 하행선으로 가지면 예외를 발생시킨다")
    @Test
    void validateSection_fail_duplicatedDownStation() {
        // given
        Sections sections = new Sections(List.of(aSection().build()));

        // when & then
        assertThatThrownBy(() -> sections.validate(new Station(3L, 지하철역), new Station(2L, 신논현역)))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("구간 추가시 하행 종점역이 아닌 역을 상행선으로 가지면 실패한다")
    @Test
    void validateSection_fail_upStationDoesNotMatchWithDownEndStation() {
        // given
        Section firstSection = aSection().build();
        Section midSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).build();
        Sections sections = new Sections(List.of(firstSection, midSection));

        // when & then
        assertThatThrownBy(() -> sections.validate(new Station(2L, 신논현역), new Station(4L, 새로운지하철역)))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("add() : 역 사이에 새로운 역을 등록에 성공한다")
    void add_insertBetweenStations() {
        Section oldSection = aSection().withDownStation(new Station(3L, 지하철역)).build();
        Sections sections = new Sections(new ArrayList<>(List.of(oldSection)));
        Section newSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역))
            .withDistance(5).build();

        sections.add(newSection);

        assertAll(
            () -> assertThat(sections.getStations()).hasSize(2),
            () -> assertThat(oldSection.getDistance()).isEqualTo(5),
            () -> assertThat(newSection.getDistance()).isEqualTo(5));
    }

    @DisplayName("add() : 새로운 역을 상행 종점으로 등록에 성공한다")
    @Test
    void add_upEndStation() {
        Section oldSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).build();
        Sections sections = new Sections(new ArrayList<>(List.of(oldSection)));
        Section newSection = aSection().build();

        sections.add(newSection);

        assertAll(
            () -> assertThat(sections.getStations()).hasSize(2),
            () -> assertThat(oldSection.getDistance()).isEqualTo(10),
            () -> assertThat(newSection.getDistance()).isEqualTo(10));
    }

    @DisplayName("add() : 새로운 역을 하행 종점으로 등록에 성공한다")
    @Test
    void add_downEndStation() {
        Section oldSection = aSection().build();
        Sections sections = new Sections(new ArrayList<>(List.of(oldSection)));
        Section newSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).build();

        sections.add(newSection);

        assertAll(
            () -> assertThat(sections.getStations()).hasSize(2),
            () -> assertThat(oldSection.getDistance()).isEqualTo(10),
            () -> assertThat(newSection.getDistance()).isEqualTo(10));
    }

    @DisplayName("add() : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록에 실패한다")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void add_fail_distanceIsTooShort(int distance) {
        Section oldSection = aSection().withDownStation(new Station(3L, 지하철역)).build();
        Sections sections = new Sections(new ArrayList<>(List.of(oldSection)));
        Section newSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역))
            .withDistance(distance).build();

        assertThatThrownBy(() -> sections.add(newSection))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 합니다.");
    }

    @DisplayName("add() : 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가에 실패한다")
    @Test
    void add_fail_alreadyRegistered() {
        Section oldSection = aSection().build();
        Sections sections = new Sections(new ArrayList<>(List.of(oldSection)));
        Section newSection = aSection().build();

        assertThatThrownBy(() -> sections.add(newSection))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("이미 등록되어 있는 구간입니다.");
    }

    @DisplayName("add() : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가에 실패한다")
    @Test
    void add_fail_noInterSection() {
        Section oldSection = aSection().build();
        Sections sections = new Sections(new ArrayList<>(List.of(oldSection)));
        Section newSection = aSection().withStations(new Station(3L, 지하철역), new Station(4L, 새로운지하철역)).build();

        assertThatThrownBy(() -> sections.add(newSection))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("상행역과 하행역 중 하나는 등록되어 있어야 합니다.");
    }

    @DisplayName("getStations() : 구간에 등록된 역 목록을 상행 종점역부터 순서대로 반환한다")
    @Test
    void getStations() {
        Section oldSection = aSection().withDownStation(new Station(3L, 지하철역)).build();
        Sections sections = new Sections(new ArrayList<>(List.of(oldSection)));
        Section newSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역))
            .withDistance(5).build();
        sections.add(newSection);

        List<Station> stations = sections.getStations();

        assertAll(
            () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
            () -> assertThat(stations.get(1).getId()).isEqualTo(2L),
            () -> assertThat(stations.get(2).getId()).isEqualTo(3L));
    }

    @DisplayName("구간 제거시 비즈니스 검증에 문제가 없다면 삭제된다")
    @Test
    void deleteSection() {
        // given
        Section firstSection = aSection().build();
        Section secondSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).build();
        Sections sections = new Sections(new ArrayList<>(List.of(firstSection, secondSection)));

        // when
        sections.delete(secondSection.getDownStation());

        // then
        assertThat(sections.getSections()).hasSize(1);
    }

    @DisplayName("구간 제거시 하행 종점역이 아니라면 실패한다")
    @Test
    void deleteSection_fail_notDownEndStation() {
        // given
        Section firstSection = aSection().build();
        Section secondSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).build();
        Sections sections = new Sections(new ArrayList<>(List.of(firstSection, secondSection)));

        // when
        assertThatThrownBy(() -> sections.delete(secondSection.getUpStation()));

        // then
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("구간 제거시 상행 종점역과 하행 종점역 뿐이라면 실패한다")
    @Test
    void deleteSection_fail_onlyOneSection() {
        // given
        Section firstSection = aSection().build();
        Sections sections = new Sections(new ArrayList<>(List.of(firstSection)));

        // when & then
        assertThatThrownBy(() -> sections.delete(firstSection.getDownStation()))
            .isInstanceOf(BusinessException.class);

        // then
        assertThat(sections.getSections()).hasSize(1);
    }
}
