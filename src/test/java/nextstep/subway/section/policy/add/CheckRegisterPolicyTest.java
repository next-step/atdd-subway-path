package nextstep.subway.section.policy.add;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CheckRegisterPolicyTest {

    private AddSectionPolicy addSectionPolicy = new CheckRegisterPolicy();

    @DisplayName("추가하려는 구간의 상행역,하행역이 이미 모두 노선 내 구간에 등록되어 있다면 validate 실패한다.")
    @Test
    void validate() {
        // given
        Station 강남역 = mock(Station.class);
        Station 신논현역 = mock(Station.class);
        given(강남역.getId()).willReturn(1L);
        given(신논현역.getId()).willReturn(2L);
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(강남역)
                        .downStation(신논현역)
                        .distance(10L)
                        .build()
        )));
        Section section = Section.builder()
                .upStation(강남역)
                .downStation(신논현역)
                .distance(11L)
                .build();

        // when
        // then
        assertThatThrownBy(() -> addSectionPolicy.validate(sections, section))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Section's stations already registered");
    }
}
