package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionCollection;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선도 조 테스트")
class SectionCollectionTest {
    Line line;
    Station firstStation;
    Station secondStation;
    Section section;


    @BeforeEach
    void init() {
        line = new Line("신분당선", "red");
        firstStation = new Station("강남역");
        secondStation = new Station("판교역");
        section = new Section(line, firstStation, secondStation, 10);

       line.addSections(section);
    }

    @Test
    @DisplayName("마지막 역을 찾을 수 있다.")
    void getLastStation() {
        Station lastStation = line.getSectionCollection().getLastStation();
        assertThat(lastStation).isEqualTo(secondStation);
    }


    @Test
    @DisplayName("첫번째 역을 찾을 수 있다.")
    void getFirstStation() {
        Station findFirstStation = line.getSectionCollection().getFirstStation();
        assertThat(findFirstStation).isEqualTo(firstStation);
    }

    @Test
    @DisplayName("상행역으로 있는 구간을 찾을 수 있따.")
    void getUpSection() {
        Optional<Section> upSection = line.getSectionCollection().getUpSection(firstStation);

        assertThat(upSection).isNotEmpty();
        assertThat(upSection.get()).isEqualTo(section);
    }

    @Test
    @DisplayName("하행역으로 있는 구간을 찾을 수 있다.")
    void getDownSection() {
        Optional<Section> downSection = line.getSectionCollection().getDownSection(secondStation);
        assertThat(downSection).isNotEmpty();
        assertThat(downSection.get()).isEqualTo(section);
    }
}