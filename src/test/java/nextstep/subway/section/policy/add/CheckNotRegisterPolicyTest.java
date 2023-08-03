package nextstep.subway.section.policy.add;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CheckNotRegisterPolicyTest {

    private AddSectionPolicy addSectionPolicy = new CheckNotRegisterPolicy();

    @DisplayName("추가하려는 구간의 상행역,하행역이 모두 노선 내 구간에 없다면 validate 실패한다.")
    @Test
    void validate() {
        // given
        Station 강남역 = mock(Station.class);
        Station 신논현역 = mock(Station.class);
        Station 논현역 = mock(Station.class);
        Station 판교역 = mock(Station.class);
        given(강남역.getId()).willReturn(1L);
        given(신논현역.getId()).willReturn(2L);
        given(논현역.getId()).willReturn(3L);
        given(판교역.getId()).willReturn(4L);
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(강남역)
                        .downStation(신논현역)
                        .distance(10L)
                        .build()
        )));
        Section section = Section.builder()
                .upStation(논현역)
                .downStation(판교역)
                .distance(11L)
                .build();
        // when
        // then
        assertThatThrownBy(() -> addSectionPolicy.validate(sections, section))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Section's stations not exist in sections");
    }
}
