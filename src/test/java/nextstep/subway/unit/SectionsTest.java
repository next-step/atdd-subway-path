package nextstep.subway.unit;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Sections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    private static final Long 종로3가역 = 1L;
    private static final Long 시청역 = 2L;
    private static final Long 서울역 = 3L;
    private static final Long 종각역 = 4L;
    private Line 노선;
    private Sections 구간리스트;

    @BeforeEach
    void setup() {
        노선 = new Line("노선", "파란색", 종로3가역, 시청역, 10);
        구간리스트 = 노선.getSections();
    }

    @Test
    @DisplayName("구간을 등록한다.")
    void 구간_등록() {
        구간리스트.addSection(new Section(노선, 시청역, 서울역, 1));

        assertThat(구간리스트.getSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("중간역이 포함된 구간을 등록한다.")
    void 중간_구간_등록() {
        구간리스트.addMidSection(노선, new Section(노선, 종로3가역, 종각역, 1));

        assertTrue(구간리스트.hasStation(종각역));
    }


    @Test
    @DisplayName("구간을 제거한다.")
    void 구간_제거() {
        Section section = new Section(노선, 시청역, 서울역, 1);
        구간리스트.addSection(section);
        구간리스트.deleteSection(section);

		assertFalse(구간리스트.hasStation(서울역));
    }

    @Test
    @DisplayName("중간역이 포함된 구간을을 제거한다.")
    void 중간_구간_제거() {
        Section section = new Section(노선, 시청역, 서울역, 1);
        구간리스트.addSection(section);
        구간리스트.deleteMidSection(노선, 시청역);

        assertFalse(구간리스트.hasStation(시청역));
    }

    @Test
    @DisplayName("구간 제거 시, 구간이 1개 밖에 없으면 삭제에 실패한다.")
    void 구간이_1개인_경우_제거_실패() {
        assertThrows(IllegalArgumentException.class,
                () -> 구간리스트.deleteSection(new Section(노선, 종로3가역, 시청역, 10)));
    }
}
