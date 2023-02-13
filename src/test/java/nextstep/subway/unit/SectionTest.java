package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_역삼_구간;
import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 구간 단위 테스트")
class SectionTest {
    // Given 노선을 생성하고
    // When 구간을 추가하면
    // Then 노선에 구간이 등록된다
    @DisplayName("지하철 노선의 구간 추가")
    @Test
    void addSection() {
        // given
        Line line = 이호선.엔티티_생성();

        // when
        Section section = 강남_역삼_구간.엔티티_생성(line);
        line.addSection(section);

        // then
        assertThat(line.getSections().isAlreadyRegisteredSection(section)).isTrue();
    }

    // Given 노선을 생성하고
    // and 구간을 추가하고
    // When 노선의 역들을 조회하면
    // Then 2개의 역이 조회된다
    @DisplayName("지하철 노선에 포함되어 있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = 이호선.엔티티_생성();
        Section section = 강남_역삼_구간.엔티티_생성(line);
        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).hasSize(2);
    }

    // Given 노선을 생성하고
    // and 구간을 추가하고
    // When 구간을 삭제하면
    // Then 구간은 비어있다
    @DisplayName("지하철 노선의 구간 삭제")
    @Test
    void removeSection() {
        // given
        Line line = 이호선.엔티티_생성();
        Section section = 강남_역삼_구간.엔티티_생성(line);
        line.addSection(section);

        // when
        line.removeLastSection(section.getDownStation());

        // then
        assertThat(line.getSections().isEmpty()).isTrue();
    }
}
