package nextstep.subway.unit.service;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.applicaion.dto.response.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] 구간 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    LineService lineService;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        long 강남역_ID = 1L;
        long 역삼역_ID = 2L;
        long lineId = 1L;

        given(stationService.findById(강남역_ID)).willReturn(new Station(강남역_ID, "강남역"));
        given(stationService.findById(역삼역_ID)).willReturn(new Station(역삼역_ID, "역삼역"));
        given(lineRepository.findById(lineId)).willReturn(Optional.of(new Line("2호선", "green")));

        lineService.addSection(lineId, new SectionRequest(강남역_ID, 역삼역_ID, 10));

        then(lineRepository).should().findById(lineId);
        List<Long> stationIds = lineService.findById(lineId)
                .getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(1L, 2L);
    }
}
