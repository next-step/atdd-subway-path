package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.fixture.ConstStation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    private static final String NEW_BUN_DANG = "신분당선";
    private static final String BG_RED_600 = "bg-red-600";

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("기존 노선에 한 구간을 추가")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Long 강남역 = stationRepository.save(ConstStation.강남역).getId();
        Long 신논현역 = stationRepository.save(ConstStation.신논현역).getId();
        Line 신분당선 = lineRepository.save(Line.of(NEW_BUN_DANG, BG_RED_600));

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), SectionRequest.of(강남역, 신논현역, 10));

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> 신분당선_구간 = 신분당선.getSections();
        List<Station> 신분당선_역 = 신분당선.allStations();

        assertAll(
                () -> assertThat(신분당선_구간).hasSize(1),
                () -> assertThat(신분당선_역).hasSize(2),
                () -> assertThat(신분당선_역).extracting("id").containsExactly(강남역, 신논현역)
        );
    }

    @Test
    @DisplayName("기존 노선에 다양한 방법으로 구간을 추가")
    void addSectionDifferentWays() {
        Long 강남역 = stationRepository.save(ConstStation.강남역).getId();
        Long 신논현역 = stationRepository.save(ConstStation.신논현역).getId();
        Long 정자역 = stationRepository.save(ConstStation.정자역).getId();
        Long 판교역 = stationRepository.save(ConstStation.판교역).getId();
        Long 이매역 = stationRepository.save(ConstStation.이매역).getId();
        Line 신분당선 = lineRepository.save(Line.of(NEW_BUN_DANG, BG_RED_600));

        // when
        // addSection 호출
        lineService.addSection(신분당선.getId(), SectionRequest.of(강남역, 신논현역, 10));
        lineService.addSection(신분당선.getId(), SectionRequest.of(강남역, 정자역, 4));
        lineService.addSection(신분당선.getId(), SectionRequest.of(판교역, 강남역, 5));
        lineService.addSection(신분당선.getId(), SectionRequest.of(신논현역, 이매역, 7));

        // then
        // line.getSections, line.allStations() 메서드를 통해 검증
        List<Section> 신분당선_구간 = 신분당선.getSections();
        List<Station> 신분당선_역 = 신분당선.allStations();

        assertAll(
                () -> assertThat(신분당선_구간).hasSize(4),
                () -> assertThat(신분당선_역).hasSize(5),
                () -> assertThat(신분당선_역).extracting("name")
                        .containsExactly("판교역", "강남역", "정자역", "신논현역", "이매역")
        );
    }

    @Test
    @DisplayName("중간 구간 제거")
    void removeMiddleSection() {
        Long 강남역 = stationRepository.save(ConstStation.강남역).getId();
        Long 신논현역 = stationRepository.save(ConstStation.신논현역).getId();
        Long 정자역 = stationRepository.save(ConstStation.정자역).getId();
        Line 신분당선 = lineRepository.save(Line.of(NEW_BUN_DANG, BG_RED_600));

        lineService.addSection(신분당선.getId(), SectionRequest.of(강남역, 신논현역, 10));
        lineService.addSection(신분당선.getId(), SectionRequest.of(신논현역, 정자역, 5));

        // when
        // removeSection 호출
        lineService.deleteSection(신분당선.getId(), 신논현역);

        // then
        // line.getSections, line.allStation 메서드를 통해 검증
        List<Section> sections = 신분당선.getSections();
        List<Station> stations = 신분당선.allStations();

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections).extracting("distance").containsExactly(15),
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).extracting("id").containsExactly(강남역, 정자역)
        );
    }
}
