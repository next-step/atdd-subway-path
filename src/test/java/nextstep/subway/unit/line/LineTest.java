package nextstep.subway.unit.line;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 단위 테스트")
class LineTest {

    private Station 강남역;
    private Station 양재역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        신분당선 = new Line("2호선", "green", 강남역, 양재역, 7);
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        Station 정자역 = new Station("정자역");
        Section section = new Section(신분당선, 강남역, 정자역, 3);

        // when
        신분당선.addSection(section);

        // then
        Assertions.assertThat(신분당선.getSections()).hasSize(2);
    }

    @DisplayName("지하철 노선의 역 조회")
    @Test
    void getStations() {
        // given
        Section section = 신분당선.getSections().get(0);

        // when
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        // then
        assertThat(upStation).isEqualTo(강남역);
        assertThat(downStation).isEqualTo(양재역);
    }

    @DisplayName("지하철 노선에 존재하는 첫번째 역을 삭제한다.")
    @Test
    void removeStationAtStart() {
        // given
        Station 정자역 = new Station("정자역");
        Section 구간_강남역_정자역 = new Section(신분당선, 강남역, 정자역, 3);
        신분당선.addSection(구간_강남역_정자역);

        // when
        신분당선.deleteSectionAtStart();

        // then
        Assertions.assertThat(신분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 노선에서 중간에 존재하는 역을 삭제한다.")
    @Test
    void removeStationAtMiddle() {
        // given
        Station 정자역 = new Station("정자역");
        Section 구간_강남역_정자역 = new Section(신분당선, 강남역, 정자역, 3);
        신분당선.addSectionAtMiddle(구간_강남역_정자역);

        // when
        신분당선.deleteSectionAtMiddle(정자역);

        // then
        Assertions.assertThat(신분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 노선에 존재하는 마지막 역을 삭제한다.")
    @Test
    void removeStationAtLast() {
        // given
        Station 정자역 = new Station("정자역");
        Section 구간_강남역_정자역 = new Section(신분당선, 강남역, 정자역, 3);
        신분당선.addSection(구간_강남역_정자역);

        // when
        신분당선.deleteSectionAtLast();

        // then
        Assertions.assertThat(신분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 노선에 존재하는 마지막 역을 삭제 시 총 길이가 줄어든다.")
    @Test
    void verifyDistanceWhenRemoveStationAtLast() {
        // given
        Station 정자역 = new Station("정자역");
        Section 구간_강남역_정자역 = new Section(신분당선, 강남역, 정자역, 3);
        신분당선.addSection(구간_강남역_정자역);

        // when
        신분당선.deleteSectionAtLast();

        // then
        int totalDistance = 신분당선.getSections().stream()
                .mapToInt(Section::getDistance)
                .sum();
        Assertions.assertThat(totalDistance).isEqualTo(7);
    }
}