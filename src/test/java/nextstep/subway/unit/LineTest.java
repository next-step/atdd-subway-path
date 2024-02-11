package nextstep.subway.unit;

import nextstep.config.fixtures.LineFixture;
import nextstep.config.fixtures.SectionFixture;
import nextstep.config.fixtures.StationFixture;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 엔티티")
class LineTest {

    Line 이호선;

    @BeforeEach
    void 초기_지하철_노선() {
        이호선 = LineFixture.이호선_생성();
    }

    /**
     * Given 지하철 노선이 생성되고
     * When  지하철 구간을 추가하면
     * Then  지하철 노선에 구간이 추가된다.
     */
    @Test
    void addSection() {
        // given
        Section 강남_양재_구간 = SectionFixture.강남_양재_구간;

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
        Section 삼성_선릉_구간 = SectionFixture.삼성_선릉_구간;

        이호선.addSection(삼성_선릉_구간);

        // when
        List<Station> 이호선_모든_역 = 이호선.getStations();

        // then
        assertThat(이호선_모든_역).containsOnly(
                삼성_선릉_구간.getUpStation(),
                삼성_선릉_구간.getDownStation());
    }

    /**
     * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
     * When  지하철 노선에 포함된 특정 역을 삭제할 경우
     * Then  특정 역이 하행역으로 추가된 구간이 삭제된다.
     */
    @Test
    void removeSection() {
        // given
        Section 삼성_선릉_구간 = SectionFixture.삼성_선릉_구간;

        이호선.addSection(삼성_선릉_구간);

        // when
        이호선.deleteSection(삼성_선릉_구간.getDownStation());

        // then
        assertThat(이호선.getSections().getSections()).doesNotContain(삼성_선릉_구간);
    }
}
