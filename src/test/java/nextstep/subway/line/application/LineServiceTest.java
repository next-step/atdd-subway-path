package nextstep.subway.line.application;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 금정역 = stationRepository.save(new Station("금정역"));
        Station 범계역 = stationRepository.save(new Station("범계역"));
        Station 평촌역 = stationRepository.save(new Station("평촌역"));
        LineResponse lineResponse = lineService.saveLine(new LineRequest("사호선", "skyblue", 금정역.getId(), 범계역.getId(), 10));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = SectionRequest.Builder()
                .upStationId(범계역.getId())
                .downStationId(평촌역.getId())
                .distance(6)
                .build();
        lineService.addSection(lineResponse.getId(),sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        LineResponse resultResponse = lineService.findLineResponseById(lineResponse.getId());
        assertThat(resultResponse.getStations()).hasSize(3);
    }
}
