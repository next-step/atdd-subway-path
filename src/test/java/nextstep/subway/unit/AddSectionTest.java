package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.strategy.add.AddSectionFactory;
import nextstep.subway.domain.strategy.add.AddSectionStrategy;
import nextstep.subway.domain.strategy.add.BasicAddSection;
import nextstep.subway.domain.strategy.add.MiddleAddSection;
import nextstep.subway.domain.strategy.remove.BasicRemoveSection;
import nextstep.subway.domain.strategy.remove.MiddleRemoveSection;
import nextstep.subway.domain.strategy.remove.RemoveSectionFactory;
import nextstep.subway.domain.strategy.remove.RemoveSectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.print.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("추가 및 삭제 전략 테스트")
public class AddSectionTest {
    Line line;
    Station firstStation;
    Station secondStation;
    Section section;

    AddSectionFactory addSectionFactory = new AddSectionFactory();
    RemoveSectionFactory removeSectionFactory = new RemoveSectionFactory();


    @BeforeEach
    void init() {
        line = new Line("신분당선", "red");
        firstStation = new Station("강남역");
        secondStation = new Station("판교역");
        section = new Section(line, firstStation, secondStation, 10);

        line.addSections(section);
    }

    @Test
    @DisplayName("가장 앞구간 삽입 또는 맨뒤의 구간 삽입일 경우에는 기본 추가 전략이 생성된다.")
    void BasicAddSectionCreateTest() {
        Station thirdStation = new Station("청계산역");
        Station fourth = new Station("논현역");
        Section firstSection = new Section(line, thirdStation, firstStation, 10);
        Section lastSection = new Section(line, secondStation, fourth, 10);

        AddSectionStrategy firstAddSection = addSectionFactory.createAddSection(line.getSectionCollection(), firstSection);
        AddSectionStrategy lastAddSection = addSectionFactory.createAddSection(line.getSectionCollection(), lastSection);

        assertThat(firstAddSection).isInstanceOf(BasicAddSection.class);
        assertThat(lastAddSection).isInstanceOf(BasicAddSection.class);
    }

    @Test
    @DisplayName("구간 사이에 삽입할 경우 MiddleAddSection 전략이 생성된다.")
    void MiddleAddSectionCreateTest() {
        Station thirdStation = new Station("청계산역");
        Section middleSection = new Section(line, firstStation, thirdStation, 2);

        AddSectionStrategy addSection = addSectionFactory.createAddSection(line.getSectionCollection(), middleSection);

        assertThat(addSection).isInstanceOf(MiddleAddSection.class);
    }

    @Test
    @DisplayName("가장 앞 지하철 또는 맨뒤의 지하철 제거시 기본 삭제 전략이 생성된다.")
    void basicRemoveSectionCreateTest() {
        Station thirdStation = new Station("청계산역");
        Station fourth = new Station("논현역");
        line.addSections(new Section(line, secondStation, thirdStation, 10));
        line.addSections(new Section(line, thirdStation, fourth, 10));

        RemoveSectionStrategy lastRemove = removeSectionFactory.createRemoveSectionStrategy(line.getSectionCollection(), fourth);
        RemoveSectionStrategy firstRemove = removeSectionFactory.createRemoveSectionStrategy(line.getSectionCollection(), firstStation);

        assertThat(lastRemove).isInstanceOf(BasicRemoveSection.class);
        assertThat(firstRemove).isInstanceOf(BasicRemoveSection.class);
    }

    @Test
    @DisplayName("중간의 지하철을 삭제할 시 중간 삭제 전략이 생성된다.")
    void middleRemoveSectionCreateTest() {
        Station thirdStation = new Station("청계산역");
        line.addSections(new Section(line, secondStation, thirdStation, 10));

        RemoveSectionStrategy removeSectionStrategy = removeSectionFactory.createRemoveSectionStrategy(line.getSectionCollection(), secondStation);

        assertThat(removeSectionStrategy).isInstanceOf(MiddleRemoveSection.class);
    }
}
