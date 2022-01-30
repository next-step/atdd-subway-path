package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @DisplayName("노선을 저장하다.")
    @Test
    void saveLine() {
        // given
        LineRequest request = new LineRequest();
        request.setName("2호선");
        request.setColor("bg-green-600");

        // when
        LineResponse lineResponse = lineService.saveLine(request);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(request.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(request.getColor())
        );
    }

    @DisplayName("구간을 추가하다.")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = lineRepository.save(new Line("2호선", "bg-red-600"));
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));

        SectionRequest request = new SectionRequest();
        request.setUpStationId(upStation.getId());
        request.setDownStationId(downStation.getId());
        request.setDistance(5);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(1);
    }
}
