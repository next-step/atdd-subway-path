package nextstep.subway.unit;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    /**
     * Given 지하철 노선이 생성되고
     * When  지하철 구간을 추가하면
     * Then  지하철 노선에 구간이 추가된다.
     */
    @Test
    void addSection() {
        // given
        Station 강남 = new Station("강남");
        Station 양재 = new Station("양재");

        Line 이호선 = new Line("이호선", "그린", 10);
        Section 강남_양재_구간 = new Section(강남, 양재, 10);

        // when
        이호선.addSection(강남_양재_구간);

        // then
        assertThat(이호선.getSections().getSections()).containsAnyOf(강남_양재_구간);
    }

    /**
     * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
     * When  지하철 노선에 포함된 지하철 역을 조회할 경우
     * Then  모든 지하철 역이 조회된다.
     */
    @Test
    void getStations() {
        // given
        Station 삼성 = new Station("삼성");
        Station 선릉 = new Station("선릉");

        Line 이호선 = new Line("이호선", "그린", 10);
        Section 삼성_선릉_구간 = new Section(삼성, 선릉, 10);

        이호선.addSection(삼성_선릉_구간);

        // when
        List<Station> 이호선_모든_역 = 이호선.getStations();

        // then
        assertThat(이호선_모든_역).containsOnly(삼성, 선릉);
    }

    /**
     * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
     * When  지하철 노선에 포함된 특정 역을 삭제할 경우
     * Then  특정 역이 하행역으로 추가된 구간이 삭제된다.
     */
    @Test
    void removeSection() {
        // given
        Station 삼성 = new Station("삼성");
        Station 선릉 = new Station("선릉");

        Line 이호선 = new Line("이호선", "그린", 10);
        Section 삼성_선릉_구간 = new Section(삼성, 선릉, 10);

        이호선.addSection(삼성_선릉_구간);

        // when
        이호선.deleteSection(선릉);

        // then
        assertThat(이호선.getSections().getSections()).doesNotContain(삼성_선릉_구간);
    }
}
