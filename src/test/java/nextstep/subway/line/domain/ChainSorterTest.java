package nextstep.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChainSorterTest {

    private List<Section> sections;

    @BeforeEach
    void setUp() {
        //Given 정렬되지 않은 구간의 역을
        sections = new ArrayList<>();
        sections.add(new Section(1L, 2L, 10L));
        sections.add(new Section(3L, 4L, 10L));
        sections.add(new Section(2L, 3L, 10L));
        sections.add(new Section(4L, 5L, 10L));
    }

    @Test
    @DisplayName("ChainSorter를 새롭게 생성해 정렬하여 반환한다")
    void returnSortedStation() {
        ChainSorter<Section, Long> sectionSorter = new ChainSorter<>(Section::getUpStationId, Section::getDownStationId);
        //when 정렬 메소드 실행시
        var sortedStationIds = sectionSorter.getSortedStationIds(sections);
        //then 순서대로 반환한다
        assertThat(sortedStationIds).containsExactly(1L, 2L, 3L, 4L, 5L);
    }


}
