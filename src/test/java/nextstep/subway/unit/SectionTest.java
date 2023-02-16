package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NotRegisteredUpStationAndDownStationException;
import nextstep.subway.exception.SectionAlreadyRegisteredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_삼성_구간;
import static nextstep.subway.fixture.SectionFixture.강남_역삼_구간;
import static nextstep.subway.fixture.SectionFixture.강남_역삼_비정상_구간;
import static nextstep.subway.fixture.SectionFixture.양재_정자_구간;
import static nextstep.subway.fixture.SectionFixture.역삼_삼성_구간;
import static nextstep.subway.fixture.StationFixture.역삼역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("지하철 구간 단위 테스트")
class SectionTest {
    /**
     * Given 노선을 생성하고
     * When 구간을 추가하면
     * Then 노선에 구간이 등록된다
     */
    @DisplayName("지하철 노선의 구간 추가")
    @Test
    void addSection() {
        // given
        Line line = 이호선.엔티티_생성();

        // when
        Section section = 강남_역삼_구간.엔티티_생성(line);
        line.addSection(section);

        // then
        assertThat(line.getAllStations()).hasSize(2);
    }

    /**
     * Given 노선을 생성하고
     * and 구간을 추가하고
     * When 노선의 역들을 조회하면
     * Then 2개의 역이 조회된다
     */
    @DisplayName("지하철 노선에 포함되어 있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = 이호선.엔티티_생성();
        Section section = 강남_역삼_구간.엔티티_생성(line);
        line.addSection(section);

        // when
        List<Station> stations = line.getAllStations();

        // then
        assertThat(stations).hasSize(2);
    }

    /**
     * When 상행역과 하행역이 이미 노선에 모두 등록되어 있다면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("지하철 구간 추가 시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 예외를 던진다")
    @Test
    void alreadyRegisteredUpStationAndDownStation() {
        // when
        Line line = 이호선.엔티티_생성();
        Section section = 강남_역삼_구간.엔티티_생성(line);
        line.addSection(section);

        // then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(SectionAlreadyRegisteredException.class);
    }

    /**
     * When 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("지하철 구간 추가 시 상행역과 하행역 둘 중 하나도 노선에 포함되어 있지 않으면 예외를 던진다")
    @Test
    void notRegisteredUpStationAndDownStation() {
        // when
        Line line = 이호선.엔티티_생성();
        Section section = 강남_역삼_구간.엔티티_생성(line);
        line.addSection(section);

        // then
        assertThatThrownBy(() -> line.addSection(양재_정자_구간.엔티티_생성(line)))
                .isInstanceOf(NotRegisteredUpStationAndDownStationException.class);
    }

    /**
     * When 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 예외를 던진다")
    @Test
    void newDistanceSameOrLongerThanOriginDistance() {
        Line line = 이호선.엔티티_생성();
        Section 등록된_강남_삼성_구간 = 강남_삼성_구간.엔티티_생성(line);
        line.addSection(등록된_강남_삼성_구간);


        assertThatThrownBy(() -> line.addSection(강남_역삼_비정상_구간.엔티티_생성(line)))
                .isInstanceOf(InvalidDistanceException.class);
    }

    /**
     * When 기존 구간 A-C에 신규 구간 A-B를 추가하면
     * Then 기존 구간의 상행역은 B로 수정된다. (A-C -> A-B-C)
     */
    @DisplayName("구간 추가 시 신규 구간의 상행역과 기존 구간의 상행역이 같은 경우 기존 구간의 상행역은 신규 구간의 하행역으로 수정된다.")
    @Test
    void addSectionToUpStation() {
        // when
        Line line = 이호선.엔티티_생성();
        Section 등록된_강남_삼성_구간 = 강남_삼성_구간.엔티티_생성(line);
        line.addSection(등록된_강남_삼성_구간);
        line.addSection(강남_역삼_구간.엔티티_생성(line));

        // then
        assertThat(등록된_강남_삼성_구간.getUpStation().getName())
                .isEqualTo(역삼역.역_이름());
    }

    /**
     * When 기존 구간 A-C에 신규 구간 B-C를 추가하면
     * Then 기존 구간의 하행역은 B로 수정된다. (A-C -> A-B-C)
     */
    @DisplayName("구간 추가 시 신규 구간의 하행역과 기존 구간의 하행역이 같은 경우 기존 구간의 하행역은 신규 구간의 상행역으로 수정된다.")
    @Test
    void addSectionToDownStation() {
        // when
        Line line = 이호선.엔티티_생성();
        Section 등록된_강남_삼성_구간 = 강남_삼성_구간.엔티티_생성(line);
        line.addSection(등록된_강남_삼성_구간);
        line.addSection(역삼_삼성_구간.엔티티_생성(line));

        // then
        assertThat(등록된_강남_삼성_구간.getDownStation().getName())
                .isEqualTo(역삼역.역_이름());
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
        assertThat(line.isEmptySections()).isTrue();
    }
}
