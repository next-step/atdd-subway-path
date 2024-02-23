package nextstep.subway.unit;

import static nextstep.subway.support.fixture.LineFixture.이호선_색;
import static nextstep.subway.support.fixture.LineFixture.이호선_이름;
import static nextstep.subway.support.fixture.LineFixture.일호선_색;
import static nextstep.subway.support.fixture.LineFixture.일호선_이름;
import static nextstep.subway.support.fixture.StationFixture.강남역_이름;
import static nextstep.subway.support.fixture.StationFixture.교대역_이름;
import static nextstep.subway.support.fixture.StationFixture.낙성대역_이름;
import static nextstep.subway.support.fixture.StationFixture.서울역_이름;
import static nextstep.subway.support.fixture.StationFixture.청량리역_이름;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domians.domain.Line;
import nextstep.subway.domians.domain.Section;
import nextstep.subway.domians.domain.Station;
import nextstep.subway.domians.repository.LineRepository;
import nextstep.subway.domians.repository.StationRepository;
import nextstep.subway.support.fixture.LineFixture;
import nextstep.subway.support.fixture.SectionFixture;
import nextstep.subway.support.fixture.StationFixture;
import nextstep.subway.web.dto.request.AddSectionRequest;
import nextstep.subway.web.dto.response.LineResponse;
import nextstep.subway.web.dto.response.LineResponse.StationDto;
import nextstep.subway.web.service.LineService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("노선 서비스 Sociable 단위 테스트")
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    Line 이호선;
    Section 강남역_교대역_구간;
    Section 교대역_낙성대역_구간;
    Station 강남역;
    Station 교대역;
    Station 낙성대역;


    Line 일호선;
    Section 서울역_청량리역_구간;
    Station 서울역;
    Station 청량리역;


    @BeforeEach
    void setUp() {

        강남역 = stationRepository.save(StationFixture.giveOne(강남역_이름));
        교대역 = stationRepository.save(StationFixture.giveOne(교대역_이름));
        낙성대역 = stationRepository.save(StationFixture.giveOne(낙성대역_이름));
        서울역 = stationRepository.save(StationFixture.giveOne(서울역_이름));
        청량리역 = stationRepository.save(StationFixture.giveOne(청량리역_이름));

        이호선 = lineRepository.save(LineFixture.giveOne(1L, 이호선_이름, 이호선_색));
        일호선 = lineRepository.save(LineFixture.giveOne(2L, 일호선_이름, 일호선_색));

        강남역_교대역_구간 = SectionFixture.giveOne(1L, 이호선, 강남역, 교대역, 10L);
        교대역_낙성대역_구간 = SectionFixture.giveOne(2L, 이호선, 교대역, 낙성대역, 10L);
        서울역_청량리역_구간 = SectionFixture.giveOne(3L, 일호선, 서울역, 청량리역, 10L);
    }

    @DisplayName("노선의 구간 추가 서비스 단위 테스트")
    @Test
    void addSection() {
        // given
        Line 이호선 = 강남역_교대역_구간_이호선();

        // when
        AddSectionRequest 교대역_낙성대역_구간_추가_요청 = new AddSectionRequest(교대역.getId(), 낙성대역.getId(), 10L);
        LineResponse result = lineService.addSection(이호선.getId(), 교대역_낙성대역_구간_추가_요청);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(result.getStations()).hasSize(3);
        });
    }

    @DisplayName("노선 단일 조회 서비스 단위 테스트")
    @Test
    void findLine() {
        // given
        Line 강남_교대_이호선 = 강남역_교대역_구간_이호선();

        // when
        LineResponse result = lineService.findLine(강남_교대_이호선.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(
                result.getStations().stream()
                    .map(StationDto::getId)
                    .collect(Collectors.toList())
            ).containsExactly(강남역.getId(), 교대역.getId());
        });
    }

    @DisplayName("노선 목록 조회 서비스 단위 테스트")
    @Test
    void findLines() {
        // given
        서울역_청량리역_구간_일호선();
        강남역_교대역_구간_이호선();

        // when
        List<LineResponse> result = lineService.findAllLines();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(result).hasSize(2);
        });
    }


    @DisplayName("노선의 구간 제거 단위 테스트")
    @Test
    void removeSection() {
        // given
        Line 이호선 = 강남역_낙성대역_구간_이호선();

        // when
        LineResponse result = lineService.removeSection(이호선.getId(), 낙성대역.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(result.getStations().stream()
                .map(StationDto::getId)
                .collect(Collectors.toList())
            ).doesNotContain(낙성대역.getId());
        });
    }


    private Line 강남역_교대역_구간_이호선() {
        이호선.addSection(강남역_교대역_구간);
        return lineRepository.save(이호선);
    }

    private Line 강남역_낙성대역_구간_이호선() {
        이호선.addSection(강남역_교대역_구간);
        이호선.addSection(교대역_낙성대역_구간);
        return lineRepository.save(이호선);
    }

    private Line 서울역_청량리역_구간_일호선() {
        일호선.addSection(서울역_청량리역_구간);
        return lineRepository.save(일호선);
    }

}
