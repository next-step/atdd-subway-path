package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line;
    private Station 수원역 = new Station("수원역");
    private Station 부산역 = new Station("부산역");

    @BeforeEach
    void setUp() {
        line = new Line("간선", "blue");
        Section section = new Section(line, 수원역, 부산역, 1);
        line.addSections(section);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        //when
        Station 서울역 = new Station("서울역");
        Section lastSection = new Section(line, 부산역, 서울역, 1);
        line.addSections(lastSection);

        //then
        assertThat(line.getStations()).containsExactly(수원역, 부산역, 서울역);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void addFirstSection() {
        //when
        Station 서울역 = new Station("서울역");
        Section lastSection = new Section(line, 서울역, 수원역, 1);
        line.addSections(lastSection);

        //then
        assertThat(line.getStations()).containsExactly(서울역, 수원역, 부산역);
    }

    @DisplayName("구간 목록 두번째에 새로운 구간을 추가할 경우")
    @Test
    void addSecondSection() {
        //when
        Station 서울역 = new Station("서울역");
        Station 부천역 = new Station("부천역");
        Section lastSection = new Section(line, 서울역, 수원역, 2);
        line.addSections(lastSection);
        Section secondSection = new Section(line, 서울역, 부천역, 1);
        line.addSections(secondSection);

        for (Station station : line.getStations()) {
            System.out.println(station.getName());
        }

        //then
        assertThat(line.getStations()).containsExactly(서울역, 부천역, 수원역, 부산역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        //then
        assertThat(line.getStations()).containsExactly(수원역, 부산역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        Station 서울역 = new Station("서울역");
        Section lastSection = new Section(line, 부산역, 서울역, 1);
        line.addSections(lastSection);

        //when
        line.removeSection(서울역);

        //then
        assertThat(line.getStations()).containsExactly(수원역, 부산역);
    }

}
