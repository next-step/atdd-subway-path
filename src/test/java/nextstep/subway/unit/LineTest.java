package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.AddSectionException;
import nextstep.subway.exception.DeleteSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.ConstStation.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    private static final String NEW_BUN_DANG = "신분당선";
    private static final String BG_RED_600 = "bg-red-600";

    private Line 신분당선;

    @BeforeEach
    void setUp() {
        신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);
    }

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);

        신분당선.addSection(강남_신논현);

        List<Section> sections = 신분당선.getSections();

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                () -> assertThat(sections.get(0).getDownStation()).isEqualTo(신논현역)
        );
    }

    @DisplayName("상행 종점역 찾기")
    @Test
    void findFirstSection() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 정자_신논현 = Section.of(정자역, 신논현역, 4);
        Section 강남_판교 = Section.of(강남역, 판교역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(정자_신논현);
        신분당선.addSection(강남_판교);

        Section findFirstSection = 신분당선.firstSection();

        assertAll(
                () -> assertThat(findFirstSection.getUpStation()).isEqualTo(강남역),
                () -> assertThat(findFirstSection.getDownStation()).isEqualTo(판교역),
                () -> assertThat(findFirstSection.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("하행 종점역 찾기")
    @Test
    void findLastSection() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 9);
        Section 신논현_판교 = Section.of(신논현역, 판교역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);
        신분당선.addSection(신논현_판교);

        Section findLastSection = 신분당선.lastSection();

        assertAll(
                () -> assertThat(findLastSection.getUpStation()).isEqualTo(판교역),
                () -> assertThat(findLastSection.getDownStation()).isEqualTo(정자역),
                () -> assertThat(findLastSection.getDistance()).isEqualTo(4)
        );
    }

    @DisplayName("새로운 구간을 맨 앞에 추가")
    @Test
    void addSectionInFront() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 7);
        Section 판교_강남 = Section.of(판교역, 강남역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);
        신분당선.addSection(판교_강남);

        List<Section> sections = 신분당선.getSections();

        assertAll(
                () -> assertThat(신분당선.firstSection()).isEqualTo(판교_강남),
                () -> assertThat(sections).hasSize(3)
        );
    }

    @DisplayName("새로운 구간을 맨 뒤에 추가")
    @Test
    void addSectionInLast() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 7);
        Section 정자_판교 = Section.of(정자역, 판교역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);
        신분당선.addSection(정자_판교);

        List<Section> sections = 신분당선.getSections();

        assertAll(
                () -> assertThat(신분당선.firstSection()).isEqualTo(강남_신논현),
                () -> assertThat(신분당선.lastSection()).isEqualTo(정자_판교),
                () -> assertThat(sections).hasSize(3)
        );
    }

    @DisplayName("새로운 구간을 노선 중간에 추가(신규 노선의 상행역과 기존 노선의 상행역 일치)")
    @Test
    void addSectionInBetweenSameUpStation() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 10);
        Section 신논현_판교 = Section.of(신논현역, 판교역, 4);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);
        신분당선.addSection(신논현_판교);

        List<Section> sections = 신분당선.getSections();

        assertAll(
                () -> assertThat(sections).hasSize(3),
                () -> assertThat(sections).extracting("upStation").containsExactly(강남역, 판교역, 신논현역),
                () -> assertThat(sections).extracting("downStation").containsExactly(신논현역, 정자역, 판교역),
                () -> assertThat(sections).extracting("distance").containsExactly(10, 6, 4)
        );
    }

    @DisplayName("새로운 구간을 노선 중간에 추가(신규 노선의 하행역과 기존 노선의 하행역 일치)")
    @Test
    void addSectionInBetweenSameDownStation() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 7);
        Section 판교_정자 = Section.of(판교역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);
        신분당선.addSection(판교_정자);

        List<Section> sections = 신분당선.getSections();

        assertAll(
                () -> assertThat(sections).hasSize(3),
                () -> assertThat(sections).extracting("upStation").containsExactly(강남역, 신논현역, 판교역),
                () -> assertThat(sections).extracting("downStation").containsExactly(신논현역, 판교역, 정자역),
                () -> assertThat(sections).extracting("distance").containsExactly(10, 2, 5)
        );
    }

    @DisplayName("역 사이에 등록할 때 기존 구간의 길이가 신규 구간의 길이보다 짧을 때 예외")
    @Test
    void addSectionShorterDistanceException() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 강남_정자 = Section.of(강남역, 정자역, 15);

        신분당선.addSection(강남_신논현);

        assertThatThrownBy(() -> 신분당선.addSection(강남_정자))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("신규 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionWhenSameUpStationAndDownStationException() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);
        Section 강남_정자 = Section.of(강남역, 정자역, 4);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        assertThatThrownBy(() -> 신분당선.addSection(강남_정자))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어 있지 않다면 추가할 수 없음")
    @Test
    void addSectionWhenNotExistsUpStationAndDownStationException() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);
        Section 판교_이매 = Section.of(판교역, 이매역, 6);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        assertThatThrownBy(() -> 신분당선.addSection(판교_이매))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("상행역과 하행역 둘 중 하나라도 노선에 존재해야 합니다.");
    }

    @DisplayName("지하철 노선에 존재하는 모든 역 조회")
    @Test
    void getStations() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        List<Station> stations = 신분당선.allStations();

        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).containsExactly(강남역, 신논현역, 정자역)
        );
    }

    @DisplayName("지하철 노선에 특정 구간 제거")
    @Test
    void removeSection() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        신분당선.removeSection(정자역);

        List<Station> stations = 신분당선.allStations();

        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 신논현역)
        );
    }

    @DisplayName("구간이 1개인 노선에서 구간을 삭제할 경우 예외")
    @Test
    void removeOnlyOneSectionException() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);

        신분당선.addSection(강남_신논현);

        assertThatThrownBy(() -> 신분당선.removeSection(신논현역))
                .isInstanceOf(DeleteSectionException.class)
                .hasMessage("구간이 1개인 노선은 구간 삭제를 진행할 수 없습니다.");
    }

    @DisplayName("삭제하려는 역이 노선에 등록되어 있지 않을 경우 예외")
    @Test
    void removeNotExistsStationException() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        assertThatThrownBy(() -> 신분당선.removeSection(판교역))
                .isInstanceOf(DeleteSectionException.class)
                .hasMessage("삭제하려는 역이 노선에 등록되지 않은 역입니다.");
    }

    @DisplayName("삭제하려는 역이 마지막 구간의 역이 아닐 경우 예외")
    @Test
    void removeNotLastStationException() {
        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        assertThatThrownBy(() -> 신분당선.removeSection(신논현역))
                .isInstanceOf(DeleteSectionException.class)
                .hasMessage("삭제하려는 역이 마지막 구간의 역이 아닙니다.");
    }
}
