package subway.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayBadRequestException;
import subway.line.model.Section;
import subway.station.model.Station;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SectionTest {

    /**
     * Given 상행역과 하행역을 가진 기존 구간이 있고
     * When 새 구간이 주어지면
     * Then 새 구간의 상행역은 기존 구간의 하행역이 된다.
     * Then 새로 들어온 구간의 길이 만큼 기존 구간의 길이가 줄어든다
     */
    @DisplayName("해당 구간의 하행에 새로운 구간을 추가")
    @Test
    void changeDownStation() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Station 선릉역 = new Station(3L, "선릉역");
        Section 기존_구간 = Section.builder().upStation(강남역).downStation(선릉역).distance(10L).build();

        // when
        Section 새구간 = Section.builder().upStation(역삼역).downStation(선릉역).distance(5L).build();
        기존_구간.changeDownStation(새구간);

        // then
        assertThat(기존_구간.getDownStation()).isEqualTo(새구간.getUpStation());

        // then
        assertThat(기존_구간.getDistance()).isEqualTo(5L);
    }

    /**
     * Given 상행역과 하행역을 가진 기존 구간이 있고
     * When 새 구간이 주어지면
     * Then 새 구간의 하행역은 기존 구간의 상행역이 된다.
     * Then 새로 들어온 구간의 길이 만큼 기존 구간의 길이가 줄어든다
     */
    @DisplayName("해당 구간의 상행에 새로운 구간을 추가")
    @Test
    void changeUpStation() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Station 선릉역 = new Station(3L, "선릉역");
        Section 기존_구간 = Section.builder().upStation(강남역).downStation(선릉역).distance(10L).build();

        // when
        Section 새구간 = Section.builder().upStation(강남역).downStation(역삼역).distance(5L).build();
        기존_구간.changeUpStation(새구간);

        // then
        assertThat(기존_구간.getUpStation()).isEqualTo(새구간.getDownStation());

        // then
        assertThat(기존_구간.getDistance()).isEqualTo(5L);
    }

    /**
     * Given 기존 구간이 있고
     * When 기존 구간 보다 긴 새 구간을 추가하려고 하면
     * Then 추가되지 않는다.
     */
    @DisplayName("기존 구간 이상의 길이는 추가되지 않음")
    @Test
    void changeStationFail() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Station 선릉역 = new Station(3L, "선릉역");
        Section 기존_구간 = Section.builder().upStation(강남역).downStation(선릉역).distance(10L).build();

        // when
        Section 새구간 = Section.builder().upStation(역삼역).downStation(선릉역).distance(11L).build();

        // then
        assertThatThrownBy(() -> 기존_구간.changeDownStation(새구간))
                .isInstanceOf(SubwayBadRequestException.class);
    }

    /**
     * Given 2개의 이어진 구간이 있고
     * When 뒷 구간의 하행역을 옮기면
     * Then 2번째 구간의 하행역을 1번째 구간의 하행역으로 옮기고
     * Then 2번째 구간의 길이 만큼 1번째 구간의 길이를 늘린다.
     */
    @DisplayName("구간의 하행역과 이어진 구간의 하행역을 해당 구간의 하행역으로 이동")
    @Test
    void pullDownStationFromUpStationOfTargetSection() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Station 선릉역 = new Station(3L, "선릉역");
        Section 앞_구간 = Section.builder().upStation(강남역).downStation(역삼역).distance(10L).build();
        Section 뒷_구간 = Section.builder().upStation(역삼역).downStation(선릉역).distance(10L).build();

        // when
        앞_구간.pullDownStationFromUpStationOfTargetSection(뒷_구간);

        // then
        assertThat(앞_구간.getDownStation()).isEqualTo(선릉역);

        // then
        assertThat(앞_구간.getDistance()).isEqualTo(20L);
    }
}