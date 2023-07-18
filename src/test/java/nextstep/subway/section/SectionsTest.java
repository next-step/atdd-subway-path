package nextstep.subway.section;

import static common.Constants.새로운지하철역;
import static common.Constants.신논현역;
import static common.Constants.지하철역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.station.Station;

@DisplayName("구간 관련")
public class SectionsTest {

    @DisplayName("마지막 순서의 구간을 만든다")
    @Test
    void createLastSection() {
        Section section = SectionBuilder.aSection().build();
        Sections sections = new Sections(new ArrayList<>(List.of(section)));
        sections.add(section.getLine(), new Station(2L, 신논현역), new Station(3L, 지하철역), 10);
        Section newSection = sections.getLastSection();

        assertAll(
            () -> assertThat(newSection.getUpStation().getId()).isEqualTo(2L),
            () -> assertThat(newSection.getDownStation().getId()).isEqualTo(3L),
            () -> assertThat(newSection.getSequence()).isEqualTo(2)
        );
    }

    @DisplayName("구간 추가시 비즈니스 검증에 문제가 없다면 아무 일도 일어나지 않는다")
    @Test
    void validateSection() {
        // given
        Sections sections = new Sections(List.of(SectionBuilder.aSection().build()));

        // when & then
        sections.validate(new Station(2L, 신논현역), new Station(3L, 지하철역));
    }

    @DisplayName("구간 추가시 이미 등록된 역을 하행선으로 가지면 예외를 발생시킨다")
    @Test
    void validateSection_fail_duplicatedDownStation() {
        // given
        Sections sections = new Sections(List.of(SectionBuilder.aSection().build()));

        // when & then
        assertThatThrownBy(() -> sections.validate(new Station(3L, 지하철역), new Station(2L, 신논현역)))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("구간 추가시 하행 종점역이 아닌 역을 상행선으로 가지면 실패한다")
    @Test
    void validateSection_fail_upStationDoesNotMatchWithDownEndStation() {
        // given
        Section firstSection = SectionBuilder.aSection().build();
        Section midSection = SectionBuilder.aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).withSequence(2).build();
        Sections sections = new Sections(List.of(firstSection, midSection));

        // when & then
        assertThatThrownBy(() -> sections.validate(new Station(2L, 신논현역), new Station(4L, 새로운지하철역)))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("구간 제거시 비즈니스 검증에 문제가 없다면 삭제된다")
    @Test
    void deleteSection() {
        // given
        Section firstSection = SectionBuilder.aSection().build();
        Section secondSection = SectionBuilder.aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).withSequence(2).build();
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
        Section firstSection = SectionBuilder.aSection().build();
        Section secondSection = SectionBuilder.aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역)).withSequence(2).build();
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
        Section firstSection = SectionBuilder.aSection().build();
        Sections sections = new Sections(new ArrayList<>(List.of(firstSection)));

        // when & then
        assertThatThrownBy(() -> sections.delete(firstSection.getDownStation()))
            .isInstanceOf(BusinessException.class);

        // then
        assertThat(sections.getSections()).hasSize(1);
    }
}
