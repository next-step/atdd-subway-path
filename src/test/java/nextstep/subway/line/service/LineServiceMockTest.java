package nextstep.subway.line.service;

import nextstep.subway.common.fixture.LineFactory;
import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.line.exception.LineNotExistException;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.repository.SectionRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.service.dto.SectionCreateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    private static final long 첫번째구간_ID = 1L;
    private static final long 두번째구간_ID = 2L;
    private static final long 강남역_ID = 1L;
    private static final long 선릉역_ID = 2L;
    private static final long 역삼역_ID = 3L;
    private static final int 첫번째구간_길이 = 10;
    private static final int 두번째구간_길이 = 20;
    private static final long LINE_ID = 1L;
    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "연두색";
    private Station 강남역;
    private Station 선릉역;
    private Station 역삼역;
    private Section 강남역_선릉역_구간;
    private Section 선릉역_역삼역_구간;
    private Line line;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private StationProvider stationProvider;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        강남역 = StationFactory.createStation(강남역_ID, "강남역");
        선릉역 = StationFactory.createStation(선릉역_ID, "선릉역");
        역삼역 = StationFactory.createStation(역삼역_ID, "선릉역");
        강남역_선릉역_구간 = SectionFactory.createSection(첫번째구간_ID, 강남역, 선릉역, 첫번째구간_길이);
        선릉역_역삼역_구간 = SectionFactory.createSection(두번째구간_ID, 선릉역, 역삼역, 두번째구간_길이);
        line = LineFactory.createLine(LINE_ID, LINE_NAME, LINE_COLOR, 강남역_선릉역_구간);

        lineService = new LineService(lineRepository, sectionRepository, stationProvider);
    }

    @Nested
    @DisplayName("addSection 테스트")
    class AddSection {
        SectionCreateRequest sectionCreateRequest;

        @BeforeEach
        void setUp() {
            sectionCreateRequest = new SectionCreateRequest(선릉역_ID, 역삼역_ID, 첫번째구간_길이);
            given(stationProvider.findById(선릉역_ID)).willReturn(선릉역);
            given(stationProvider.findById(역삼역_ID)).willReturn(역삼역);
            given(sectionRepository.save(any())).willReturn(선릉역_역삼역_구간);
        }

        @Test
        @DisplayName("section 을 추가 할 수 있다.")
        void addSectionTest() {
            // given
            given(lineRepository.findByIdWithSections(LINE_ID)).willReturn(Optional.of(line));

            // when
            lineService.addSection(LINE_ID, sectionCreateRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간.getDistance() + 선릉역_역삼역_구간.getDistance());
                softly.assertThat(line.getStations()).containsExactly(강남역, 선릉역, 역삼역);
            });
        }

        @Test
        @DisplayName("section 을 추가 시 line 이 존재하지 않으면 LineNotExistException 이 던져진다.")
        void addSectionFailWhenLineNotExistTest() {
            // given
            given(lineRepository.findByIdWithSections(LINE_ID)).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> lineService.addSection(LINE_ID, sectionCreateRequest))
                    .isInstanceOf(LineNotExistException.class);

        }
    }

    @Nested
    @DisplayName("removeSection 테스트")
    class RemoveSection {
        @BeforeEach
        void setUp() {
            line.addSection(선릉역_역삼역_구간);
            given(stationProvider.findById(역삼역_ID)).willReturn(역삼역);
        }

        @Test
        @DisplayName("section 을 제거 할 수 있다.")
        void removeSection() {
            // given
            given(lineRepository.findByIdWithSections(LINE_ID)).willReturn(Optional.of(line));

            // when
            lineService.removeSection(LINE_ID, 역삼역_ID);

            // then
            assertSoftly(softly -> {
                softly.assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간.getDistance());
                softly.assertThat(line.getStations()).containsExactly(강남역, 선릉역);
            });
        }

        @Test
        @DisplayName("section 을 제거 시 line 이 존재하지 않으면 LineNotExistException 이 던져진다.")
        void removeSectionFailWhenLineNotExistTest() {
            // given
            given(lineRepository.findByIdWithSections(LINE_ID)).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> lineService.removeSection(LINE_ID, 역삼역_ID))
                    .isInstanceOf(LineNotExistException.class);

        }
    }

}
