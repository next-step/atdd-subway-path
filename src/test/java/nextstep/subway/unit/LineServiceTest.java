package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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

    private int givenDistance;

    @Test
    @DisplayName("지하철 2호선에 구간을 추가할 수 있다")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        String 강남역_이름 = "강남역";
        String 선릉역_이름 = "선릉역";

        Station 강남역 = new Station(강남역_이름);
        Station 선릉역 = new Station(선릉역_이름);
        givenDistance = 20;

        Line givenLine = new Line("2호선", "bg-green-35");

        stationRepository.save(강남역);
        stationRepository.save(선릉역);

        lineRepository.save(givenLine);

        // when
        // lineService.addSection 호출
        lineService.addSection(givenLine.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), givenDistance));

        // then
        // line.getSections 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(givenLine.getId());
        assertThat(lineResponse)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "2호선")
            .hasFieldOrPropertyWithValue("color", "bg-green-35");

        assertThat(lineResponse.getStations())
            .extracting(StationResponse::getName)
            .containsExactly(강남역_이름, 선릉역_이름);
    }
}
