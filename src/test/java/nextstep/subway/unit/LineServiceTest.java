package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {
    private static final String SEOUL_STATION = "서울역";
    private static final String YONGSAN_STATION = "용산역";
    private static final String LINE_ONE = "1호선";
    private static final String BACKGROUND_COLOR_BLUE = "bg-color-blue";
    private static final int DISTANCE_FIVE = 5;
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
        Station upStation = stationRepository.save(new Station(SEOUL_STATION));
        Station downStation = stationRepository.save(new Station(YONGSAN_STATION));
        Line line = lineRepository.save(new Line(LINE_ONE, BACKGROUND_COLOR_BLUE));

        SectionRequest sectionRequest = getSectionRequest(upStation, downStation, DISTANCE_FIVE);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Assertions.assertThat(line.getSections().getSections().contains(new Section(line, upStation, downStation, DISTANCE_FIVE))).isTrue();
    }

    private SectionRequest getSectionRequest(Station upStation, Station downStation, int distance) {
        return SectionRequest.builder()
                .upStationId(upStation.getId())
                .downStationId(downStation.getId())
                .distance(distance)
                .build();
    }
}
