package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionDeleteAction;
import nextstep.subway.exception.CanNotDeleteSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionDeleteActionTest {

    private static final long STATION_ID_1 = 0L;
    private static final long STATION_ID_2 = 1L;
    private static final long STATION_ID_3 = 2L;

    private List<Section> sections;

    @BeforeEach
    void setUp() {
        sections = new ArrayList(Arrays.asList(
                createSection(0L, STATION_ID_1, STATION_ID_2, 10, 0),
                createSection(0L, STATION_ID_2, STATION_ID_3, 2, 1)
        ));
    }

    @Test
    void 구간이_하나면_삭제_실패() {
        sections = new ArrayList(Collections.singletonList(createSection(STATION_ID_1, STATION_ID_2)));

        assertThatThrownBy(() -> SectionDeleteAction.of(sections, STATION_ID_1)).isInstanceOf(CanNotDeleteSectionException.class);
    }

    @Test
    void 일치하는_구간이_없으면_삭제_실패() {
        assertThatThrownBy(() -> SectionDeleteAction.of(sections, 3L)).isInstanceOf(CanNotDeleteSectionException.class);
    }

    @Test
    void 처음_구간_삭제_액션_생성() {
        SectionDeleteAction action = SectionDeleteAction.of(sections, STATION_ID_1);
        assertThat(action).isEqualTo(SectionDeleteAction.UP_STATION);
    }

    @Test
    void 처음_구간_삭제() {
        SectionDeleteAction action = SectionDeleteAction.of(sections, STATION_ID_1);
        action.delete(sections, STATION_ID_1);

        assertThat(sections).hasSize(1);
        Section section = sections.get(0);
        assertThat(section.getUpStationId()).isEqualTo(STATION_ID_2);
        assertThat(section.getDownStationId()).isEqualTo(STATION_ID_3);
    }

    @Test
    void 중간_구간_삭제_액션_생성() {
        SectionDeleteAction action = SectionDeleteAction.of(sections, STATION_ID_2);
        assertThat(action).isEqualTo(SectionDeleteAction.MIDDLE_STATION);
    }

    @Test
    void 중간_구간_삭제() {
        SectionDeleteAction action = SectionDeleteAction.of(sections, STATION_ID_2);
        action.delete(sections, STATION_ID_2);

        assertThat(sections).hasSize(1);
        Section section = sections.get(0);
        assertThat(section.getUpStationId()).isEqualTo(STATION_ID_1);
        assertThat(section.getDownStationId()).isEqualTo(STATION_ID_3);
        assertThat(section.getDistance()).isEqualTo(12);
    }

    @Test
    void 마지막_구간_삭제_액션_생성() {
        SectionDeleteAction action = SectionDeleteAction.of(sections, STATION_ID_3);
        assertThat(action).isEqualTo(SectionDeleteAction.DOWN_STATION);
    }

    @Test
    void 마지막_구간_삭제() {
        SectionDeleteAction action = SectionDeleteAction.of(sections, STATION_ID_3);
        action.delete(sections, STATION_ID_3);

        assertThat(sections).hasSize(1);
        Section section = sections.get(0);
        assertThat(section.getUpStationId()).isEqualTo(STATION_ID_1);
        assertThat(section.getDownStationId()).isEqualTo(STATION_ID_2);
    }

}