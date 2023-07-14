package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

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

    private Station 강남역;
    private Station 양재역;
    private Line 신분당선;

    @BeforeEach
    public void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
    }

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 등록한다")
    void addSection() {
        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // then
        assertThat(신분당선.getSectionsSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선에 새로운역을 상행종점으로 등록한다")
    void addSectionUpStation() {
        // given
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));
        Station 다른_지하철역 = stationRepository.save(new Station("다른지하철역"));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(다른_지하철역.getId(), 강남역.getId(), 15));

        // then
        assertThat(신분당선.getSectionsSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선에 새로운역을 하행종점으로 등록한다")
    void addSectionDownStation() {
        // given
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));
        Station 다른_지하철역 = stationRepository.save(new Station("다른지하철역"));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 다른_지하철역.getId(), 5));

        // then
        assertThat(신분당선.getSectionsSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되지 않은 구간을 노선에 등록하면 에러가 발생한다")
    void addSectionUpStationOrDownStationNotContain() {
        // given
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 7));
        Station 불광역 = stationRepository.save(new Station("불광역"));
        Station 독바위역 = stationRepository.save(new Station("독바위역"));

        // when, then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), new SectionRequest(불광역.getId()
                , 독바위역.getId(), 6)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("잘못된 지하철 구간 등록입니다.");
    }

    @Test
    @DisplayName("이미 등록된 상행역과 하행역을 가진 구간을 등록하는 경우 에러가 발생한다")
    void addSectionExistUpStationAndDownStationCaseOne() {
        // given
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 7));

        // when, then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId()
                , new SectionRequest(강남역.getId(), 양재역.getId(), 5)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("상행역과 하행역이 이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 구간보다 같거나 큰 경우 에러 발생한다")
    void addSectionDistanceGreaterOrEqual() {
        // given
        Station 다른_지하철역 = stationRepository.save(new Station("다른지하철역"));

        // when, then
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        assertThatThrownBy(() -> lineService.addSection(신분당선.getId()
                , new SectionRequest(강남역.getId(), 다른_지하철역.getId(), 10)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("기존 역 사이 길이보다 크거나 같을 수 없습니다.");

        assertThatThrownBy(() -> lineService.addSection(신분당선.getId()
                , new SectionRequest(강남역.getId(), 다른_지하철역.getId(), 11)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 등록되어 있는 경우 에러 발생한다")
    void addSectionExistUpStationAndDownStationCaseTwo() {
        // given
        Station 다른_지하철역 = stationRepository.save(new Station("다른지하철역"));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 4));
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 다른_지하철역.getId(), 3));

        // when, then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId()
                , 다른_지하철역.getId(), 3)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("상행역과 하행역이 이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("지하철 노선의 역 사이에 새로운 역을 등록할 경우 지하철 역목록 조회")
    void getStations() {
        Station 고속터미널역 = stationRepository.save(new Station("고속터미널역"));
        Station 교대역 = stationRepository.save(new Station("교대역"));

        // when
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 14));
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 고속터미널역.getId(), 10));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 교대역.getId(), 10));

        // then
        assertThat(신분당선.getStations().stream().mapToLong(Station::getId))
                .containsExactly(강남역.getId(), 교대역.getId(), 양재역.getId(), 고속터미널역.getId());
    }

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 제거한다")
    void deleteSection() {
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        lineService.deleteSection(신분당선.getId(), 양재역.getId());

        // then
        assertThat(신분당선.getSectionsSize()).isEqualTo(0);
    }
}
