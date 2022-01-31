package nextstep.subway.unit.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line;
    private Section section;
    private Station 서초역;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    void setFixtures() {
        line = new Line("2호선", "bg-red-600");
        서초역 = new Station("서초역");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        section = new Section(line, 교대역, 역삼역, 10);
        line.addSection(section);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionInLast() {
        // when
        line.addSection(new Section(line, 역삼역, 삼성역, 5));

        // then
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInFirst() {
        // when
        line.addSection(new Section(line, 서초역, 교대역, 5));

        // then
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInBetween() {
        // when
        line.addSection(new Section(line, 교대역, 강남역, 3));

        // then
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
    }
}
