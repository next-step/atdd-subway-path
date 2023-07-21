package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BadRequestSectionsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.fixture.StationFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 단위 테스트")
class LineTest {

    private final String COLOR_RED = "bg-red-600";

    private final String COLOR_BLUE = "bg-blue-600";
    private final int DISTANCE = 10;

    private Station 신사역, 논현역, 신논현역, 광교역, 강남역;
    private Line 신분당선;
    @BeforeEach
    void set(){
        신사역 = 지하철역_생성("신사역");
        논현역 = 지하철역_생성("논현역");
        신논현역 = 지하철역_생성("신논현역");
        강남역 = 지하철역_생성("강남역");
        광교역 = 지하철역_생성("광교역");

        신분당선 = new Line("신분당선", COLOR_RED, 논현역, 강남역, 30);
    }

    @DisplayName("노선의 앞에 구간 추가")
    @Test
    void addFirstSection() {
        Section newSection = new Section(신분당선, 신사역, 논현역, DISTANCE);
        신분당선.addSections(newSection);

        assertThat(신분당선.getStations()).containsExactly(신사역,논현역,강남역);
    }

    @DisplayName("노선의 중간에 구간 추가")
    @Test
    void addMiddleSection() {
        Section newSection = new Section(신분당선, 논현역, 신논현역, DISTANCE);
        신분당선.addSections(newSection);

        assertThat(신분당선.getStations()).containsExactly(논현역,신논현역,강남역);
    }

    @DisplayName("노선의 마지막에 구간 추가")
    @Test
    void addLastSection() {
        Section newSection = new Section(신분당선, 강남역, 광교역, DISTANCE);
        신분당선.addSections(newSection);

        assertThat(신분당선.getStations()).containsExactly(논현역,강남역,광교역);
    }

    @DisplayName("기존 구간의 길이보다 긴 새로운 구간을 추가할 수 없다")
    @Test
    void addToLongSection() {
        Section newSection = new Section(신분당선, 논현역, 신논현역,100);

        Assertions.assertThrows(BadRequestSectionsException.class,()-> 신분당선.addSections(newSection));
    }

    @DisplayName("구간의 상행역과 하행역이 모두 노선에 등록되지 않은 경우 구간 추가할 수 없다")
    @Test
    void addNotMatchSection() {
        Section newSection = new Section(신분당선, 신사역, 신논현역,DISTANCE);

        Assertions.assertThrows(BadRequestSectionsException.class,()-> 신분당선.addSections(newSection));
    }

    @DisplayName("구간의 상행역과 하행역이 모두 노선에 등록되지 않은 경우 구간 추가할 수 없다")
    @Test
    void addAlreadyRegisteredSection() {
        Section newSection = new Section(신분당선, 논현역, 강남역,DISTANCE);

        Assertions.assertThrows(BadRequestSectionsException.class,()-> 신분당선.addSections(newSection));
    }

    @DisplayName("지하철 노선에서 역 목록 가져오기")
    @Test
    void getStations() {
        assertThat(신분당선.getStations()).contains(논현역,강남역);
    }

    @DisplayName("지하철 노선에서 구간 제거")
    @Test
    void removeSection() {
        Section newSection = new Section(신분당선, 강남역, 광교역, DISTANCE);
        신분당선.addSections(newSection);

        신분당선.removeSections(광교역);

        assertThat(신분당선.getStations()).doesNotContain(광교역);
    }
}
