package nextstep.subway.line.domain;

import nextstep.subway.line.exception.OnlyOneSectionRemainingException;
import nextstep.subway.line.exception.StationAlreadyExistsException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {
    private Station 강남역 = new Station("강남역");
    private Station 양재역 = new Station("양재역");
    private Station 정자역 = new Station("정자역");
    private Station 미금역 = new Station("미금역");
    private Line 신분당선 = new Line("신분당선", "bg-red-600", 강남역, 정자역, 10);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(정자역, "id", 2L);
        ReflectionTestUtils.setField(미금역, "id", 3L);
    }

    @Test
    void getStations() {
        assertThat(신분당선.getStations()).isEqualTo(Arrays.asList(강남역, 정자역));
    }

    @Test
    void addSection() {
        int before = 신분당선.getSections().size();

        Section section = new Section(신분당선, 정자역, 미금역,10);
        신분당선.addSection(section);

        int after = 신분당선.getSections().size();

        assertThat(after).isEqualTo(before + 1);
    }


    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        신분당선.addSection(new Section(신분당선, 정자역, 미금역, 7));
        Section section = new Section(신분당선, 미금역, 강남역, 3);

        assertThatThrownBy( () -> 신분당선.addSection(section)).isInstanceOf(StationAlreadyExistsException.class);
    }

    @DisplayName("역과 역 사이에 새로운 역 추가")
    @Test
    void addSectionInMiddle() {
        신분당선.addSection(new Section(신분당선, 양재역, 정자역, 9));

        assertThat(신분당선.getStations()).isEqualTo(Arrays.asList(강남역, 양재역, 정자역));
    }

    @Test
    void removeSection() {
        신분당선.addSection(new Section(신분당선, 정자역, 미금역, 7));

        int before = 신분당선.getStations().size();
        신분당선.removeSection(미금역.getId());

        int after = 신분당선.getStations().size();

        assertThat(신분당선.getStations()).doesNotContain(미금역);
        assertThat(after).isEqualTo(before - 1);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        assertThatThrownBy( () -> 신분당선.removeSection(정자역.getId())).isInstanceOf(OnlyOneSectionRemainingException.class);
    }
}
