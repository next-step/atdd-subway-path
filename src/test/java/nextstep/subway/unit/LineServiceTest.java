package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.utils.LineTestFixtures;
import nextstep.subway.utils.StationTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;


    private Station 지하철역_저장(final String stationName) {
        final Station 지하철역 = StationTestFixtures.지하철역_생성(stationName);
        return stationRepository.save(지하철역);
    }

    private Line 노선_저장(final String 노선명, final String 노선색, final Station 상행역, final Station 하행역, final int 거리) {
        return lineRepository.save(LineTestFixtures.노선_생성(노선명, 노선색, 상행역, 하행역, 거리));
    }

    @DisplayName("지하철 노선의 구간 사이에 구간 등록하기")
    @Test
    void addSection() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        SectionRequest 두번째_구간_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 구로디지털단지역.getId(), 6);

        // when
        lineService.addSection(신분당선.getId(), 두번째_구간_요청);

        // then
        assertThat(신분당선.sectionSize()).isEqualTo(2);
    }

    @DisplayName("지하철 노선 사이에 구간 등록 시 기존 구간의 거리보다 클 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionGreatorThanSectionDistance() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 구로디지털단지역.getId(), 11);

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선 사이에 구간 등록 시 기존 구간의 거리와 동일할 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionEqualsThanSectionDistance() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 구로디지털단지역.getId(), 10);

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선 사이에 구간 등록 시 구간의 거리가 0 혹은 음수일경우")
    @Test
    void throwsExceptionIfAddSectionDistanceZeroOrNegative() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역,10);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 구로디지털단지역.getId(), 0);

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("지하철 노선의 구간과 등록할 구간이 같을경우")
    @Test
    void throwsExceptionIfEqualsAddSection() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 시청역.getId(), 10);

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("지하철 노선의 구간 가장 상위 구간에 구간을 등록할 경우")
    @Test
    void addSectionToTopSection() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 선릉역 = 지하철역_저장("선릉역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(선릉역.getId(), 강남역.getId(), 10);

        // when
        boolean isAdded = lineService.addSection(신분당선.getId(), 구간_생성_요청);

        // when
        assertThat(isAdded).isTrue();
        assertThat(신분당선.sectionSize()).isEqualTo(2);
    }

    @DisplayName("지하철 노선의 구간 가장 하위 구간에 구간을 등록할 경우")
    @Test
    void addSectionToDownSection() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 선릉역 = 지하철역_저장("선릉역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 선릉역.getId(), 5);

        // when
        boolean isAdded = lineService.addSection(신분당선.getId(), 구간_생성_요청);

        // then
        assertThat(isAdded).isTrue();
        assertThat(신분당선.sectionSize()).isEqualTo(2);
    }

    @DisplayName("지하철 노선의 구간 중 일치하지 않은 상행, 하행을 가진 구간을 등록할 경우")
    @Test
    void throwsExceptionIfNotHasStations() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 선릉역 = 지하철역_저장("선릉역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(선릉역.getId(), 구로디지털단지역.getId(), 20);

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("노선의 모든 지하철역 조회")
    @Test
    void getStations() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");
        final Station 선릉역 = 지하철역_저장("선릉역");

        final Line 신분당선 = 노선_저장("신분당선", "green", 시청역, 강남역, 10);

        final SectionRequest 첫번째_구간_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 구로디지털단지역.getId(), 5);
        final SectionRequest 두번째_구간_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 선릉역.getId(), 3);

        lineService.addSection(신분당선.getId(), 첫번째_구간_요청);
        lineService.addSection(신분당선.getId(), 두번째_구간_요청);

        // when
        LineResponse response = lineService.findById(신분당선.getId());

        // then
        assertThat(response.getStations().size()).isEqualTo(4);
        assertThat(response.getStations().stream()
                .map(StationResponse::getId)
                .collect(toList())).containsExactly(시청역.getId(), 구로디지털단지역.getId(), 강남역.getId(), 선릉역.getId());
    }

    @DisplayName("지하철 노선의 상행 구간 제거하기")
    @Test
    void removeSectionToTopSection() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final Line 신분당선 = 노선_저장("신분당선", "green", 강남역, 시청역, 10);
        신분당선.addSection(구로디지털단지역, 강남역, 5);

        // when
        boolean isRemoved = lineService.removeSection(신분당선.getId(), 구로디지털단지역.getId());

        // then
        assertThat(isRemoved).isTrue();
        LineResponse 노선_요청_응답 = lineService.findById(신분당선.getId());
        assertThat(노선_요청_응답.getStations().stream()
                .map(StationResponse::getId)
                .collect(toList())).containsExactly(강남역.getId(), 시청역.getId());
    }

    @DisplayName("지하철 노선의 하행 구간 제거하기")
    @Test
    void removeSectionToDownSection() {

        // given
        final Station 강남역 = 지하철역_저장("강남역");
        final Station 시청역 = 지하철역_저장("시청역");
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        final Line 신분당선 = 노선_저장("신분당선", "red", 강남역, 시청역, 10);

        SectionRequest 두번째_구간_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 구로디지털단지역.getId(), 5);
        lineService.addSection(신분당선.getId(), 두번째_구간_요청);

        // when
        boolean isRemoved = lineService.removeSection(신분당선.getId(), 구로디지털단지역.getId());

        // then
        assertThat(isRemoved).isTrue();
        LineResponse 노선_요청_응답 = lineService.findById(신분당선.getId());
        assertThat(노선_요청_응답.getStations().stream()
                .map(StationResponse::getId)
                .collect(toList())).containsExactly(강남역.getId(), 시청역.getId());
    }

    @DisplayName("지하철 노선의 중간 구간 제거하기")
    @Test
    void removeSectionToMiddleSection() {

        // given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");

        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "green", 강남역, 시청역, 10);
        신분당선.addSection(강남역, 구로디지털단지역, 4);

        // when
        boolean isRemoved = lineService.removeSection(신분당선.getId(), 구로디지털단지역.getId());

        // then
        assertThat(isRemoved).isTrue();
        LineResponse 노선_요청_응답 = lineService.findById(신분당선.getId());
        assertThat(노선_요청_응답.getStations().stream()
                .map(StationResponse::getId)
                .collect(toList())).containsExactly(강남역.getId(), 시청역.getId());
    }

    @DisplayName("지하철 노선의 구간이 1개일 때 구간 제거 시 예외")
    @Test
    void throwsExceptionRemoveSectionIfSectionCountOne() {

        // given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");

        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "green", 강남역, 시청역, 10);

        // then
        assertThatThrownBy(() -> lineService.removeSection(신분당선.getId(), 강남역.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제거하려는 구간이 지하철의 노선에 존재하지 않을경우 예외")
    @Test
    void throwsExceptionRemoveSectionIfHasNotSection() {

        // given
        final Station 강남역 = StationTestFixtures.지하철역_생성("강남역");
        final Station 시청역 = StationTestFixtures.지하철역_생성("시청역");
        final Station 구로디지털단지역 = StationTestFixtures.지하철역_생성("구로디지털단지역");
        final Station 선릉역 = StationTestFixtures.지하철역_생성("선릉역");

        final Line 신분당선 = LineTestFixtures.노선_생성("신분당선", "green", 강남역, 시청역, 10);
        신분당선.addSection(구로디지털단지역, 강남역, 5);

        // then
        assertThatThrownBy(() -> lineService.removeSection(신분당선.getId(), 선릉역.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

}
