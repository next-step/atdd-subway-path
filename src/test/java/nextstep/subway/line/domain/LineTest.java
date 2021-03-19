package nextstep.subway.line.domain;

import nextstep.subway.exception.ExistUpAndDownStationException;
import nextstep.subway.exception.InValidSectionSizeException;
import nextstep.subway.exception.InvalidSectionDistanceException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선에 역 등록 관련 기능 도메인 테스트")
public class LineTest {

    Station 강남역;
    Station 역삼역;
    Station 삼성역;
    Station 교대역;

    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        교대역 = new Station("교대역");
        이호선 = new Line("이호선", "green");
    }

    @Test
    void getStations() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations).contains(강남역, 역삼역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionToEnd() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when
        이호선.addSection(역삼역, 삼성역, 10);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(2);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionToFront() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when
        이호선.addSection(삼성역, 강남역, 10);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(2);
        assertThat(이호선.getStations()).containsExactly(삼성역, 강남역, 역삼역);
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addLineSectionBetween() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when
        이호선.addSection(강남역, 삼성역, 6);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(2);
        assertThat(이호선.getStations()).containsExactly(강남역, 삼성역, 역삼역);
    }

    @DisplayName("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void addLineSectionBetweenInLongerDistance() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // then
        assertThatThrownBy(() -> 이호선.addSection(강남역, 삼성역, 11))
                .isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 있다면 등록할 수 없다.")
    @Test
    void addLineSectionWithSameStations() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // then
        assertThatThrownBy(() -> 이호선.addSection(강남역, 역삼역, 9))
                .isInstanceOf(ExistUpAndDownStationException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되지 않으면 등록할 수 없다.")
    @Test
    void addLineSectionNotIncludeAny() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // then
        assertThatThrownBy(() -> 이호선.addSection(삼성역, 교대역, 9))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제외한다")
    @Test
    void removeSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 삼성역, 10);

        // when
        이호선.removeSection(삼성역);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(1);
        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("지하철 노선에 첫번째 지하철역을 제외한다")
    @Test
    void removeFirstSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 삼성역, 10);

        // when
        이호선.removeSection(강남역);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(1);
        assertThat(이호선.getStations()).containsExactly(역삼역, 삼성역);
    }

    @DisplayName("지하철 노선에 가운데 지하철역을 제외한다")
    @Test
    void removeMiddleSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 삼성역, 10);

        // when
        이호선.removeSection(역삼역);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(1);
        assertThat(이호선.getStations()).containsExactly(강남역, 삼성역);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // then
        assertThatThrownBy(() -> 이호선.removeSection(삼성역))
                .isInstanceOf(InValidSectionSizeException.class);
    }
}
