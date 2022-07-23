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

    @DisplayName("구간 등록하기")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        final String 강남역_이름 = "강남역";
        final Station 강남역 = new Station(강남역_이름);

        final String 시청역_이름 = "시청역";
        final Station 시청역 = new Station(시청역_이름);

        Station 저장된_강남역 = stationRepository.save(강남역);
        Station 저장된_시청역 = stationRepository.save(시청역);

        final String 신분당선_이름 = "신분당선";
        final String red = "red";
        final Line 신분당선 = Line.makeLine(신분당선_이름, red);

        Line 저장된_신분당선 = lineRepository.save(신분당선);

        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 저장된_강남역.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 저장된_시청역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(저장된_신분당선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(저장된_신분당선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간 제거하기")
    @Test
    void removeSection() {

        //given
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        final String 강남역_이름 = "강남역";
        final Station 강남역 = new Station(강남역_이름);

        final String 시청역_이름 = "시청역";
        final Station 시청역 = new Station(시청역_이름);

        final String 구로디지털단지역_이름 = "구로디지털단지역";
        final Station 구로디지털단지역 = new Station(구로디지털단지역_이름);

        Station 저장된_강남역 = stationRepository.save(강남역);
        Station 저장된_시청역 = stationRepository.save(시청역);
        Station 저장된_구로디지털단지역 = stationRepository.save(구로디지털단지역);

        final String 신분당선_이름 = "신분당선";
        final String red = "red";
        final Line 신분당선 = Line.makeLine(신분당선_이름, red);

        Line 저장된_신분당선 = lineRepository.save(신분당선);

        SectionRequest 첫번째_구간_요청 = new SectionRequest();
        ReflectionTestUtils.setField(첫번째_구간_요청, "upStationId", 저장된_강남역.getId());
        ReflectionTestUtils.setField(첫번째_구간_요청, "downStationId", 저장된_시청역.getId());
        ReflectionTestUtils.setField(첫번째_구간_요청, "distance", 10);

        SectionRequest 두번째_구간_요청 = new SectionRequest();
        ReflectionTestUtils.setField(두번째_구간_요청, "upStationId", 저장된_시청역.getId());
        ReflectionTestUtils.setField(두번째_구간_요청, "downStationId", 저장된_구로디지털단지역.getId());
        ReflectionTestUtils.setField(두번째_구간_요청, "distance", 5);

        lineService.addSection(저장된_신분당선.getId(), 첫번째_구간_요청);
        lineService.addSection(저장된_신분당선.getId(), 두번째_구간_요청);

        //when
        lineService.removeSection(저장된_신분당선.getId(), 저장된_구로디지털단지역.getId());

        //then
        assertThat(저장된_신분당선.getSections().size()).isEqualTo(1);

    }
}
