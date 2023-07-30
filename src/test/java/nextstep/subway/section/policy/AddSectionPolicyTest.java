package nextstep.subway.section.policy;

import nextstep.fixture.StationFixture;
import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.fixture.SectionFixture.강남역_TO_신논현역;
import static nextstep.fixture.SectionFixture.신논현역_TO_논현역;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class AddSectionPolicyTest {

    @DisplayName("기존 역 사이 길이보다 추가하려는 구간의 길이가 크거나 같으면 validate 실패한다.")
    @Test
    void validate_fail_too_long() {
        // given
        Sections sections = new Sections(List.of(강남역_TO_신논현역(), 신논현역_TO_논현역()));
        Section section = Section.builder()
                .upStation(StationFixture.신논현역())
                .downStation(new Station("판교역"))
                .distance(신논현역_TO_논현역().getDistance() + 1L)
                .build();

        // when
        // then
        assertThatThrownBy(() -> AddSectionPolicy.validate(sections, section))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Section's distance too long");
    }

}
