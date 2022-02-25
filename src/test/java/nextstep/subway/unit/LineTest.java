package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayException;

class LineTest {

    private Station 강남역;
    private Station 양재역;
    private Station 정자역;
    private Line line;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        정자역 = new Station("정자역");
        line = new Line("신분당선", "bg-red-600");
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionInLast() {
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 양재역, 정자역, 10));

        Section section = line.allSections().stream()
            .filter(it -> it.getUpStation() == 양재역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertThat(line.allStations()).containsExactly(강남역, 양재역, 정자역);
        assertThat(section.getDownStation()).isEqualTo(정자역);
        assertThat(section.getLine().allSections()).extracting(Section::getDistance).containsExactly(10, 10);
    }

    @DisplayName("구간 목록 맨처음에 새로운 구간을 추가할 경우")
    @Test
    void addSectionInFirst() {
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 정자역, 강남역, 10));

        Section section = line.allSections().stream()
            .filter(it -> it.getDownStation() == 강남역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertThat(line.allStations()).containsExactly(정자역, 강남역, 양재역);
        assertThat(section.getUpStation()).isEqualTo(정자역);
        assertThat(section.getLine().allSections()).extracting(Section::getDistance).containsExactly(10, 10);
    }

    @DisplayName("구간 중간에 상행역을 기준으로 새로운 구간을 추가")
    @Test
    void addSectionInMiddleByUpSection() {
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 강남역, 정자역, 5));

        Section section = line.allSections().stream()
            .filter(it -> it.getUpStation() == 강남역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertThat(line.allStations()).containsExactly(강남역, 정자역, 양재역);
        assertThat(section.getDownStation()).isEqualTo(정자역);
        assertThat(section.getLine().allSections()).extracting(Section::getDistance).containsExactly(5, 5);
    }

    @DisplayName("구간 중간에 하행역을 기준으로 새로운 구간을 추가")
    @Test
    void addSectionInMiddleBYDownSection() {
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 정자역, 양재역, 5));

        Section section = line.allSections().stream()
            .filter(it -> it.getUpStation() == 강남역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertThat(line.allStations()).containsExactly(강남역, 정자역, 양재역);
        assertThat(section.getDownStation()).isEqualTo(정자역);
        assertThat(section.getLine().allSections()).extracting(Section::getDistance).containsExactly(5, 5);
    }

    @DisplayName("등록할 수 없는 구간길이를 등록했을때, 예외발생 확인")
    @Test
    void addSectionWithWrongDistance() {
        line.addSection(new Section(line, 강남역, 양재역, 10));
        assertThatThrownBy(() -> {
            line.addSection(new Section(line, 정자역, 양재역, 10));
        }).isInstanceOf(SubwayException.WrongParameterException.class);
    }

    @DisplayName("중복된 구간을 등록했을때, 예외발생 확인")
    @Test
    void addSectionWithWrongSection() {
        line.addSection(new Section(line, 강남역, 양재역, 10));
        assertThatThrownBy(() -> {
            line.addSection(new Section(line, 강남역, 양재역, 10));
        }).isInstanceOf(SubwayException.DuplicatedException.class);
    }

    @DisplayName("구간의 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        // 구간이 2개 이상이도록 추가
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 양재역, 정자역, 10));

        // when
        line.deleteSection(정자역);

        // then
        assertThat(line.allStations()).containsExactly(강남역, 양재역);
    }

    @DisplayName("구간의 목록에서 첫번째 역 삭제 ")
    @Test
    void removeFirstSection() {
        // given
        // 구간이 2개 이상이도록 추가
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 양재역, 정자역, 10));

        // when
        line.deleteSection(강남역);

        // then
        assertThat(line.allStations()).containsExactly(양재역, 정자역);
    }

    @DisplayName("구간의 목록에서 중간 역 삭제")
    @Test
    void removeMiddleSection() {
        // given
        // 구간이 2개 이상이도록 추가
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 양재역, 정자역, 10));

        // when
        line.deleteSection(양재역);

        // then
        Section section = line.allSections().stream()
            .filter(it -> it.getUpStation() == 강남역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertThat(line.allStations()).containsExactly(강남역, 정자역);
        assertThat(section.getLine().allSections()).extracting(Section::getDistance).containsExactly(20);
    }

    @DisplayName("구간이 하나일때는 역을 제거할 수 없음")
    @Test
    void removeSectionWhenHasOnlySection() {
        // given
        // 구간을 1개만 등록
        line.addSection(new Section(line, 강남역, 양재역, 10));

        assertThatThrownBy(
            () -> {
                line.deleteSection(양재역);
            }
        ).isInstanceOf(SubwayException.CanNotDeleteException.class);
    }

    @DisplayName("존재하지 않는 역 제거할 수 없음")
    @Test
    void removeNotExistStation() {
        // given
        Station 역삼역 = new Station("역삼역");
        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 양재역, 정자역, 10));

        assertThatThrownBy(
            () -> {
                line.deleteSection(역삼역);
            }
        ).isInstanceOf(SubwayException.CanNotDeleteException.class);
    }
}
