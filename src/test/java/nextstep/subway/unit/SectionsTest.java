package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SectionsTest {

    private Station 사당역;
    private Station 방배역;
    private Line 이호선;
    private Section 사당_방배_구간;
    private Sections 구간들;

    @BeforeEach
    void setUp() {
        사당역 = new Station("사당");
        방배역 = new Station("방배");
        이호선 = new Line("2호선", "green");
        사당_방배_구간 = new Section(이호선, 사당역, 방배역, 10);
        구간들 = new Sections();
    }


    @DisplayName("등록된 구간이 없을 때 새로운 구간을 추가할 경우 성공한다")
    @Test
    void section_should_be_added_to_line_when_there_is_no_section() {
        // when
        구간들.addSection(사당_방배_구간);

        // then
        assertThat(구간들.getStations()).containsExactly(사당역, 방배역);
    }

    @DisplayName("마지막 구간에 새로운 구간을 등록할 경우 성공한다")
    @Test
    void section_should_be_added_to_line_when_there_is_last_section_that_is_able_to_connect() {
        // given
        Station 서초역 = new Station("서초");
        Section 방배_서초_구간 = new Section(이호선, 방배역, 서초역, 3);
        구간들.addSection(사당_방배_구간);

        // when
        구간들.addSection(방배_서초_구간);

        // then
        assertThat(구간들.getStations()).containsExactly(사당역, 방배역, 서초역);
    }

    @DisplayName("중간 구간에 상행역이 등록되어 있는 새로운 구간을 추가할 경우 성공한다")
    @Test
    void section_should_be_added_to_line_when_there_is_middle_section_that_up_station_is_registered() {
        // given
        Station 서초역 = new Station("서초");
        Section 사당_서초_구간 = new Section(이호선, 사당역, 서초역, 3);
        구간들.addSection(사당_방배_구간);

        // when
        구간들.addSection(사당_서초_구간);

        // then
        assertThat(구간들.getStations()).containsExactly(사당역, 서초역, 방배역);
    }

    @DisplayName("중간 구간에 하행역이 등록되어 있는 새로운 구간을 추가할 경우 성공한다")
    @Test
    void section_should_be_added_to_line_when_there_is_middle_section_that_down_station_is_registered() {
        // given
        Station 서초역 = new Station("서초");
        Section 서초_방배_구간 = new Section(이호선, 서초역, 방배역, 3);
        구간들.addSection(사당_방배_구간);

        // when
        구간들.addSection(서초_방배_구간);

        // then
        assertThat(구간들.getStations()).containsExactly(사당역, 서초역, 방배역);
    }

    @DisplayName("중간 구간에 길이가 더 긴 새로운 구간을 추가할 경우 실패한다")
    @Test
    void section_should_throw_exception_when_request_with_longer_section() {
        // given
        Station 서초역 = new Station("서초");
        Section 서초_방배_구간 = new Section(이호선, 서초역, 방배역, 10);
        구간들.addSection(사당_방배_구간);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> 구간들.addSection(서초_방배_구간)
        );
    }

    @DisplayName("요청한 구간의 상하행역 모두 노선에 등록된 경우 구간 등록시 실패한다")
    @Test
    void section_should_throw_exception_when_request_with_both_already_registered_station() {
        // given
        구간들.addSection(사당_방배_구간);
        Section 사당_방배_구간2 = new Section(이호선, 사당역, 방배역, 10);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> 구간들.addSection(사당_방배_구간2)
        );
    }

    @DisplayName("요청한 구간의 상하행역 모두 노선에 등록되지 않은 경우 구간 등록시 실패한다")
    @Test
    void section_should_throw_exception_when_request_with_both_unregistered_station() {
        // given
        Station 서초역 = new Station("서초");
        Station 교대역 = new Station("교대");
        Section 서초_교대_구간 = new Section(이호선, 서초역, 교대역, 10);
        구간들.addSection(사당_방배_구간);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> 구간들.addSection(서초_교대_구간)
        );
    }

    @DisplayName("요청한 구간의 하행역과 기존의 상행종점역과 동일할 때 새로운 구간을 상행 종점역으로 추가할 경우 성공한다")
    @Test
    void section_should_be_added_to_line_when_request_to_connect_as_first_up_station() {
        // given
        Station 서초역 = new Station("서초");
        Section 서초_사당_구간 = new Section(이호선, 서초역, 사당역, 3);
        구간들.addSection(사당_방배_구간);

        // when
        구간들.addSection(서초_사당_구간);

        // then
        assertThat(구간들.getStations()).containsExactly(서초역, 사당역, 방배역);
    }

    @DisplayName("마지막 구간에 연결되지 못하는 새로운 구간을 추가할 경우 실패한다")
    @Test
    void add_section_should_throw_exception_when_there_is_section_that_is_not_able_to_connect() {
        // given
        Station 서초역 = new Station("서초");
        Station 교대역 = new Station("교대");
        Section 서초_교대_구간 = new Section(이호선, 서초역, 교대역, 5);
        구간들.addSection(사당_방배_구간);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> 구간들.addSection(서초_교대_구간)
        );
    }

    @DisplayName("노선에 속해있는 역 목록 조회한다.")
    @Test
    void getStations() {
        // given
        구간들.addSection(사당_방배_구간);

        // when
        List<Station> stations = 구간들.getStations();

        // then
        assertThat(stations).containsExactly(사당역, 방배역);
    }

    @DisplayName("구간이 여러 개일 경우 마지막 역 삭제 성공한다")
    @Test
    void should_be_success_when_last_section_is_removable() {
        // given
        Station 서초역 = new Station("서초");
        Section 방배_서초_구간 = new Section(이호선, 방배역, 서초역, 5);
        구간들.addSection(사당_방배_구간);
        구간들.addSection(방배_서초_구간);

        // when
        구간들.removeStation(서초역);

        // then
        assertThat(구간들.getStations()).doesNotContain(서초역);
    }

    @DisplayName("구간이 여러 개일 경우 중간 역 삭제 성공한다")
    @Test
    void should_be_success_when_middle_section_is_removable() {
        // given
        Station 서초역 = new Station("서초");
        Section 방배_서초_구간 = new Section(이호선, 방배역, 서초역, 5);
        구간들.addSection(사당_방배_구간);
        구간들.addSection(방배_서초_구간);

        // when
        구간들.removeStation(방배역);

        // then
        assertThat(구간들.getStations()).doesNotContain(방배역);
    }

    @DisplayName("구간이 여러 개일 경우 처음 역 삭제 성공한다")
    @Test
    void should_be_success_when_first_section_is_removable() {
        // given
        Station 서초역 = new Station("서초");
        Section 방배_서초_구간 = new Section(이호선, 방배역, 서초역, 5);
        구간들.addSection(사당_방배_구간);
        구간들.addSection(방배_서초_구간);

        // when
        구간들.removeStation(사당역);

        // then
        assertThat(구간들.getStations()).doesNotContain(사당역);
    }

    @DisplayName("구간이 하나일 때 마지막 역 삭제 실패한다")
    @Test
    void should_be_fail_when_section_is_only_one() {
        // given
        구간들.addSection(사당_방배_구간);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> 구간들.removeStation(방배역)
        );
    }

    @DisplayName("등록되지 않은 역은 삭제 실패한다")
    @Test
    void should_be_fail_when_request_with_unregistered_section() {
        // given
        Station 서초역 = new Station("서초");
        Section 방배_서초_구간 = new Section(이호선, 방배역, 서초역, 5);
        구간들.addSection(사당_방배_구간);
        구간들.addSection(방배_서초_구간);
        Station 교대역 = new Station("교대");

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> 구간들.removeStation(교대역)
        ).withMessageContaining("등록되지 않은 역입니다");
    }
}



