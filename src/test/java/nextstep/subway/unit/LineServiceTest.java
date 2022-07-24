package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

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


    public Station 지하철역_생성(final String stationName) {
        return new Station(stationName);
    }

    private Station 지하철역_저장(final String stationName) {
        final Station 지하철역 = 지하철역_생성(stationName);
        return stationRepository.save(지하철역);
    }

    private Line 노선_생성(final String lineName, final String color) {
        return Line.makeLine(lineName, color);
    }

    private Line 노선_저장(final String lineName, final String color) {
        return lineRepository.save(노선_생성(lineName, color));
    }

    private SectionRequest 구간요청_생성(long upStationId, long downStationId, int distance) {
        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", upStationId);
        ReflectionTestUtils.setField(sectionRequest, "downStationId", downStationId);
        ReflectionTestUtils.setField(sectionRequest, "distance", distance);
        return sectionRequest;
    }

    @DisplayName("구간 등록하기")
    @Test
    void addSection() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Line 신분당선 = 노선_저장("신분당선", "red");
        SectionRequest sectionRequest = 구간요청_생성(강남역.getId(), 시청역.getId(), 10);

        // when
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간 제거하기")
    @Test
    void removeSection() {

        //given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final Line 신분당선 = 노선_저장("신분당선", "red");

        SectionRequest 첫번째_구간_요청 = 구간요청_생성(강남역.getId(), 시청역.getId(), 10);
        SectionRequest 두번째_구간_요청 = 구간요청_생성(시청역.getId(), 구로디지털단지역.getId(), 5);

        lineService.addSection(신분당선.getId(), 첫번째_구간_요청);
        lineService.addSection(신분당선.getId(), 두번째_구간_요청);

        //when
        lineService.removeSection(신분당선.getId(), 구로디지털단지역.getId());

        //then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }
}
