package nextstep.subway.unit;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 서비스 단위 테스트 without mock")
@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    StationService stationService;

    @Autowired
    LineService lineService;

    StationResponse 강남역_생성_응답;
    StationResponse 역삼역_생성_응답;
    StationResponse 선릉역_생성_응답;
    LineResponse 이호선_생성_응답;

    @BeforeEach
    void setUp() {
        강남역_생성_응답 = stationService.saveStation(new StationRequest("강남역"));
        역삼역_생성_응답 = stationService.saveStation(new StationRequest("역삼역"));
        선릉역_생성_응답 = stationService.saveStation(new StationRequest("선릉역"));
        이호선_생성_응답 = lineService.save(new LineRequest("이호선", "green", 강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10));
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        // when
        lineService.addSection(이호선_생성_응답.getId(), new SectionRequest(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 10));

        // then
        assertThat(lineService.findLineById(이호선_생성_응답.getId())).isNotNull();
        assertThat(lineService.findLineById(이호선_생성_응답.getId()).getId()).isNotNull();
        assertThat(lineService.findLineById(이호선_생성_응답.getId()).getStations().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList())
        ).containsExactly(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 선릉역_생성_응답.getId());
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        lineService.addSection(이호선_생성_응답.getId(), new SectionRequest(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 10));

        // when
        lineService.deleteSection(이호선_생성_응답.getId(), 선릉역_생성_응답.getId());

        // then
        assertThat(lineService.findLineById(이호선_생성_응답.getId())).isNotNull();
        assertThat(lineService.findLineById(이호선_생성_응답.getId()).getId()).isNotNull();
        assertThat(lineService.findLineById(이호선_생성_응답.getId()).getStations().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList())
        ).containsExactly(강남역_생성_응답.getId(), 역삼역_생성_응답.getId());
    }
}
