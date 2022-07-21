package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static nextstep.subway.utils.EntityCreator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        final long lineId = 12345L;
        final long upStationId = 123L;
        final long downStationId = 234L;

        Line 신분당선 = createLine();
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Station 미금역 = createStation("미금역");
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);
        신분당선.addSection(판교_정자);

        given(lineRepository.findById(lineId)).willReturn(Optional.of(신분당선));
        given(stationService.findById(upStationId)).willReturn(정자역);
        given(stationService.findById(downStationId)).willReturn(미금역);

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, getSectionRequest(upStationId, downStationId));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineRepository.findById(lineId).get();
        Section 정자_미금 = createSection(신분당선, 정자역, 미금역);

        assertThat(line).isNotNull();
        assertThat(line.getStations()).hasSize(3)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 판교역, 미금역));
        assertThat(line.getSections()).hasSize(2);
        assertThat(line.getSections().stream().anyMatch(s -> sectionEquals(s, 정자_미금))).isTrue();
    }

    private SectionRequest getSectionRequest(long upStationId, long downStationId) {
        return new SectionRequest(upStationId, downStationId, 10);
    }

    private boolean sectionEquals(Section one, Section two) {
        return Objects.equals(one.getLine(), two.getLine()) &&
                Objects.equals(one.getUpStation(), two.getUpStation()) && Objects.equals(one.getDownStation(), two.getDownStation());
    }
}
