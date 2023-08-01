package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.dto.SectionDto;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SectionServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @InjectMocks
    private SectionService sectionService;

    private Station 당고개역, 이수역, 사당역;

    @BeforeEach
    void setUpStation() {
        당고개역 = 당고개역();
        이수역 = 이수역();
        사당역 = 사당역();
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Line line = line(당고개역, 이수역);

        SectionDto sectionDto
                = sectionDto(이수역.getId(), 사당역.getId());
        given(lineRepository.findById(sectionDto.getId())).willReturn(Optional.of(line));
        given(stationRepository.findById(이수역.getId())).willReturn(Optional.of(이수역));
        given(stationRepository.findById(사당역.getId())).willReturn(Optional.of(사당역));

        // when
        sectionService.addSection(line.getId(), sectionDto);

        // then
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "이수역", "사당역"
                );
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        SectionDto sectionDto
                = sectionDto(이수역.getId(), 사당역.getId());
        given(lineRepository.findById(sectionDto.getId())).willReturn(Optional.of(line));
        given(stationRepository.findById(이수역.getId())).willReturn(Optional.of(이수역));
        given(stationRepository.findById(사당역.getId())).willReturn(Optional.of(사당역));
        sectionService.addSection(line.getId(), sectionDto);

        // when : 기능 수행
        sectionService.removeSection(line.getId(), 사당역.getId());

        // then : 결과 확인
        assertThat(line.getSections().getSections()).hasSize(1)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("당고개역", "이수역")
                );
    }

    private Line line(Station upStation, Station downStation) {
        Line line = Line.builder()
                .name("4호선")
                .color("blue")
                .upStation(upStation)
                .downStation(downStation)
                .section(section(upStation, downStation))
                .build();
        ReflectionTestUtils.setField(line, "id", 1L);
        return line;
    }

    private Station 당고개역() {
        Station 당고개역 = new Station("당고개역");
        ReflectionTestUtils.setField(당고개역, "id", 1L);
        return 당고개역;
    }

    private Station 이수역() {
        Station 이수역 = new Station("이수역");
        ReflectionTestUtils.setField(이수역, "id", 2L);
        return 이수역;
    }

    private Station 사당역() {
        Station 사당역 = new Station("사당역");
        ReflectionTestUtils.setField(사당역, "id", 3L);
        return 사당역;
    }

    private Section section(Station upStation, Station downStation) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .build();
    }

    private SectionDto sectionDto(Long upStationId, Long downStationId) {
        SectionDto sectionDto = SectionDto.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(10)
                .build();
        ReflectionTestUtils.setField(sectionDto, "id", 1L);
        return sectionDto;
    }
}
