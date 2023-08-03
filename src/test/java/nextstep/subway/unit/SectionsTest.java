package nextstep.subway.unit;

import nextstep.subway.section.policy.add.AddSectionPolicy;
import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static nextstep.fixture.SectionFixture.강남역_TO_신논현역;
import static nextstep.fixture.SectionFixture.신논현역_TO_논현역;
import static nextstep.fixture.StationFixture.논현역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SectionsTest {

    private AddSectionPolicy addSectionPolicy = mock(AddSectionPolicy.class);

    @Test
    void addSection() {
        // given
        Station 강남역 = mock(Station.class);
        Station 신논현역 = mock(Station.class);
        Station 논현역 = mock(Station.class);
        given(강남역.getId()).willReturn(1L);
        given(신논현역.getId()).willReturn(2L);
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(강남역)
                        .downStation(신논현역)
                        .distance(10L)
                        .build()
        )));

        // when
        sections.addSection(Section.builder()
                .upStation(신논현역)
                .downStation(논현역)
                .distance(5L)
                .build(), addSectionPolicy);

        // then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void addSection_change_firstStationId() {
        // given
        Station 강남역 = mock(Station.class);
        Station 신논현역 = mock(Station.class);
        Station 논현역 = mock(Station.class);
        given(강남역.getId()).willReturn(1L);
        given(신논현역.getId()).willReturn(2L);
        given(논현역.getId()).willReturn(3L);
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(신논현역)
                        .downStation(논현역)
                        .distance(5L)
                        .build()
        )));

        // when
        sections.addSection(Section.builder()
                .upStation(강남역)
                .downStation(신논현역)
                .distance(10L)
                .build(), addSectionPolicy);

        // then
        assertThat(sections.getFirstStationId()).isEqualTo(1L);
    }

    @Test
    void addSection_change_lastStationId() {
        // given
        Station 강남역 = mock(Station.class);
        Station 신논현역 = mock(Station.class);
        Station 논현역 = mock(Station.class);
        given(강남역.getId()).willReturn(1L);
        given(신논현역.getId()).willReturn(2L);
        given(논현역.getId()).willReturn(3L);
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(강남역)
                        .downStation(신논현역)
                        .distance(10L)
                        .build()
        )));

        // when
        sections.addSection(Section.builder()
                .upStation(신논현역)
                .downStation(논현역)
                .distance(5L)
                .build(), addSectionPolicy);

        // then
        assertThat(sections.getLastStationId()).isEqualTo(3L);
    }

    @Test
    void getAllStation() {
        // given
        Station 강남역 = mock(Station.class);
        Station 신논현역 = mock(Station.class);
        Station 논현역 = mock(Station.class);
        given(강남역.getId()).willReturn(1L);
        given(신논현역.getId()).willReturn(2L);
        given(논현역.getId()).willReturn(3L);
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(강남역)
                        .downStation(신논현역)
                        .distance(10L)
                        .build(),
                Section.builder()
                        .upStation(신논현역)
                        .downStation(논현역)
                        .distance(5L)
                        .build()
        )));

        // when
        List<Station> stations = sections.getAllStation();

        // then
        assertThat(stations).isEqualTo(List.of(강남역, 신논현역, 논현역));
    }

    @Test
    void deleteSectionByLastStation() {
        // given
        Sections sections = new Sections(new ArrayList<>(List.of(
                강남역_TO_신논현역(),
                신논현역_TO_논현역()
        )));

        // when
        sections.deleteSectionByLastStation(논현역());

        // then
        assertThat(sections.size()).isOne();
    }
}
