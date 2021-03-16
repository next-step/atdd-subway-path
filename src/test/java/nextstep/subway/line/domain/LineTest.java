package nextstep.subway.line.domain;

import nextstep.subway.exception.InValidSectionSizeException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선에 역 등록 관련 기능 도메인 테스트")
public class LineTest {

    Station 강남역, 역삼역, 삼성역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
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


    @Test
    void removeSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 삼성역, 10);

        // when
        이호선.removeSection(삼성역);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(1);
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
