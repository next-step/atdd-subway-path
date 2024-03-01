package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;
import nextstep.subway.exception.IllegalSectionException;

class LineTest {
    @Test
    void addSection_노선에_구간을_추가할_수_있다() {
        //given
        Station upStation = new Station("역삼역");
        Station downStation = new Station("선릉역");
        Section section = new Section(upStation, downStation, 10);
        Line line = new Line("이호선", "초록색");
        //when
        line.addSection(section);

        //then
        Sections sections = line.getSections();
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(1),
            () -> assertThat(sections.getLastSection()).isEqualTo(section)
        );
    }

    @Test
    void addSection_기존_노선의_하행종점역과_추가_구간의_상행역이_다르면_예외를_반환한다() {
        //given
        Station 역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 1L);
        Station 선릉역 = new Station("선릉역");
        ReflectionTestUtils.setField(선릉역, "id", 2L);
        Section existingSection = new Section(역삼역, 선릉역, 10);
        Line line = new Line("이호선", "초록색");
        line.addSection(existingSection);

        //when
        Station newUpStation = new Station("삼성역");
        Station newDownStation = new Station("잠실역");
        Section newSection = new Section(newUpStation, newDownStation, 2);

        //then
        assertThatThrownBy(() -> line.addSection(newSection))
            .isInstanceOf(IllegalSectionException.class);
    }

    @Test
    void getStations() {
        //given
        Station upStation = new Station("역삼역");
        Station downStation = new Station("선릉역");
        Section existingSection = new Section(upStation, downStation, 10);
        Line line = new Line("이호선", "초록색");
        line.addSection(existingSection);

        //when
        Stations stations = line.getStations();

        assertAll(
            () -> assertThat(stations.getStations()).hasSize(2),
            () -> assertThat(stations.getStations()).contains(upStation, downStation)
        );

    }

    @Test
    void testDeleteLastSection_노선에_구간이_2개_이상_등록되어_있을_때_구간을_제거할_수_있다() {
        Station 역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 1L);
        Station 선릉역 = new Station("선릉역");
        ReflectionTestUtils.setField(선릉역, "id", 2L);
        Station 잠실역 = new Station("잠실역");
        ReflectionTestUtils.setField(잠실역, "id", 3L);

        Section 역삼_선릉_구간 = new Section(역삼역, 선릉역, 10);
        Section 선릉_잠실_구간 = new Section(선릉역, 잠실역, 2);
        String lineName = "이호선";
        String lineColor = "초록색";
        Line line = new Line(lineName, lineColor);
        line.addSection(역삼_선릉_구간);
        line.addSection(선릉_잠실_구간);

        //when
        line.deleteSection(잠실역);

        //then
        Sections sections = line.getSections();
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(1),
            () -> assertThat(sections.getSections().get(0)).isEqualTo(역삼_선릉_구간)
        );
    }

    @Test
    void testUpdateLine_노선의_이름과_색을_변경할_수_있다() {
        //given
        Line line = new Line("이호선", "초록색");
        //when
        line.updateLine("분당선", "노란색");
        //then
        assertAll(
            () -> assertThat(line.getName()).isEqualTo("분당선"),
            () -> assertThat(line.getColor()).isEqualTo("노란색")
        );
    }

    @Test
    void testUpdateLine_노선의_이름과_색중에_적어도_하나가_null이면_예외를_반환한다() {
        //given
        Line line = new Line("이호선", "초록색");
        //when
        assertAll(
            () -> assertThatThrownBy(() ->
                                         line.updateLine(null, "노란색")).isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() ->
                                         line.updateLine("분당선", null)).isInstanceOf(IllegalArgumentException.class)
        );
    }

}
