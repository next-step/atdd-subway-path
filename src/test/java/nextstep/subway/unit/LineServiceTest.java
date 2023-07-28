package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("구간 서비스 단위 테스트 without Mock")
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("지하철 노선 구간 추가")
    void addSection() {
        // given
        Station 봉천역 = stationRepository.save(new Station("봉천역"));
        Station 신림역 = stationRepository.save(new Station("신림역"));
        Line line = lineRepository.save(new Line("2호선", "#00FF00"));

        SectionRequest request = SectionRequest.builder()
            .upStationId(봉천역.getId())
            .downStationId(신림역.getId())
            .distance(10)
            .build();

        // when
        lineService.addNewSection(line.getId(), request);

        // then
        Assertions.assertThat(line.getStations())
            .asList()
            .containsExactly(봉천역, 신림역);
    }

    @Test
    @DisplayName("지하철 노선 구간 추가 - 역 사이에 새로운 역 추가 (상행역 기준)")
    void addSectionBetween_상행역_기준() {

    }

    @Test
    @DisplayName("지하철 노선 구간 추가 - 역 사이에 새로운 역 추가(하행역 기준)")
    void addSectionBetween_하행역_기준() {

    }
}
