package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.AddSectionException;
import nextstep.subway.exception.DeleteSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.ConstStation.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    private static final String NEW_BUN_DANG = "신분당선";
    private static final String BG_RED_600 = "bg-red-600";

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

        Section section = Section.of(강남역, 신논현역, 10);

        신분당선.addSection(section);

        List<Section> sections = 신분당선.getSections();

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                () -> assertThat(sections.get(0).getDownStation()).isEqualTo(신논현역)
        );
    }

    @DisplayName("지하철 구간 추가시 종점역과 신규 상행역이 일치 하지 않을 경우 예외")
    @Test
    void unmatchedLastStationAndNewUpStationException() {
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 정자_판교역 = Section.of(정자역, 판교역, 5);

        신분당선.addSection(강남_신논현);

        assertThatThrownBy(() -> 신분당선.addSection(정자_판교역))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("기존 노선의 종점역과 신규 노선의 상행역이 일치하지 않습니다.");
    }

    @DisplayName("지하철 구간 추가시 기존 노선에 존재하는 역일 경우 예외")
    @Test
    void alreadyExistsStationException() {
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_강남 = Section.of(신논현역, 강남역, 5);

        신분당선.addSection(강남_신논현);

        assertThatThrownBy(() -> 신분당선.addSection(신논현_강남))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("신규 구간의 하행역이 기존 노션의 역에 이미 등록되어 있습니다.");
    }

    @DisplayName("지하철 노선에 존재하는 모든 역 조회")
    @Test
    void getStations() {
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

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
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

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
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);

        신분당선.addSection(강남_신논현);

        assertThatThrownBy(() -> 신분당선.removeSection(신논현역))
                .isInstanceOf(DeleteSectionException.class)
                .hasMessage("구간이 1개인 노선은 구간 삭제를 진행할 수 없습니다.");
    }

    @DisplayName("삭제하려는 역이 노선에 등록되어 있지 않을 경우 예외")
    @Test
    void removeNotExistsStationException() {
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

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
        Line 신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);

        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        assertThatThrownBy(() -> 신분당선.removeSection(신논현역))
                .isInstanceOf(DeleteSectionException.class)
                .hasMessage("삭제하려는 역이 마지막 구간의 역이 아닙니다.");
    }
}
