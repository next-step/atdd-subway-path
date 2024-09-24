package nextstep.subway.path.api;

import nextstep.subway.path.api.response.PathResponse;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.navercorp.fixturemonkey.api.experimental.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    StationRepository stationRepository;

    @Mock
    SectionRepository sectionRepository;

    @InjectMocks
    PathService pathService;

    static Station 강남역;
    static Station 역삼역;
    static Station 선릉역;
    static Station 선정릉역;
    static Station 강남구청역;
    static Station 학동역;
    static Station 논현역;
    static Station 런던역;
    static Station 파리역;
    static List<Section> 구간들;

    @BeforeEach
    public void setUp() {
        /**
         *                         3             1
         *                 논현 --------- 학동 --------- 강남구청
         *                  |                          |
         *                  |                          |   3
         *              4   |                         선정릉
         *                  |                          |
         *                  |                          |   1
         *                 강남 --------- 역삼 --------- 선릉
         *                         1            2
         *
         *                 런던 --------- 파리
         *                       10000
         */

        강남역 = 역_생성("강남역");
        역삼역 = 역_생성("역삼역");
        선릉역 = 역_생성("선릉역");
        선정릉역 = 역_생성("선정릉역");
        강남구청역 = 역_생성("강남구청역");
        학동역 = 역_생성("학동역");
        논현역 = 역_생성("논현역");
        런던역 = 역_생성("런던역");
        파리역 = 역_생성("파리역");

        구간들 = List.of(
                구간_생성(강남역.getId(), 역삼역.getId(), 1),
                구간_생성(역삼역.getId(), 선릉역.getId(), 2),
                구간_생성(선릉역.getId(), 선정릉역.getId(),  1),
                구간_생성(선정릉역.getId(), 강남구청역.getId(), 3),
                구간_생성(강남구청역.getId(), 학동역.getId(), 1),
                구간_생성(학동역.getId(), 논현역.getId(), 3),
                구간_생성(논현역.getId(), 강남역.getId(), 4),
                구간_생성(파리역.getId(), 런던역.getId(), 10000)
        );
    }

    @Test
    void 경로에_포함된_역과_총거리를_반환한다() {
        // given
        given(stationRepository.findAll()).willReturn(List.of(강남역, 역삼역, 선릉역, 선정릉역, 강남구청역, 학동역, 논현역, 런던역, 파리역));
        given(sectionRepository.findAll()).willReturn(구간들);
        given(stationRepository.findById(강남역.getId())).willReturn(Optional.of(강남역));
        given(stationRepository.findById(선정릉역.getId())).willReturn(Optional.of(선정릉역));

        // when
        PathResponse response = pathService.getPath(강남역.getId(), 선정릉역.getId());

        // then
        assertThat(response.getDistance()).isEqualTo(4);
        assertThat(response.getStations()).containsExactly(
                StationResponse.of(강남역),
                StationResponse.of(역삼역),
                StationResponse.of(선릉역),
                StationResponse.of(선정릉역)
        );
    }

    @Test
    void 출발역과_종착역이_연결되지_않으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(강남역.getId())).willReturn(Optional.of(강남역));
        given(stationRepository.findById(런던역.getId())).willReturn(Optional.of(런던역));
        given(sectionRepository.findAll()).willReturn(구간들);

        // when && then
        assertThatThrownBy(() -> pathService.getPath(강남역.getId(), 런던역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 종착역이 연결되어 있지 않습니다.");
    }

    @Test
    void 출발역이_없으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(강남역.getId())).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> pathService.getPath(강남역.getId(), 런던역.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("출발역을 찾을 수 없습니다.");
    }

    @Test
    void 종착역이_없으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(강남역.getId())).willReturn(Optional.of(강남역));
        given(stationRepository.findById(런던역.getId())).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> pathService.getPath(강남역.getId(), 런던역.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("종착역을 찾을 수 없습니다.");
    }

    @Test
    void 출발역과_종착역이_같으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(강남역.getId())).willReturn(Optional.of(강남역));

        // when && then
        assertThatThrownBy(() -> pathService.getPath(강남역.getId(), 강남역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 종착역이 같습니다.");
    }

    @Test
    void 최단_경로_조회에_성공한다() {
        // given
        given(stationRepository.findAll()).willReturn(List.of(강남역, 역삼역, 선릉역, 선정릉역, 강남구청역, 학동역, 논현역, 런던역, 파리역));
        given(sectionRepository.findAll()).willReturn(구간들);

        // when
        PathResponse pathResponse = pathService.findPath(강남역.getId(), 선정릉역.getId());

        // then
        assertThat(pathResponse.getDistance()).isEqualTo(4);
        assertThat(pathResponse.getStations()).containsAnyOf(
                StationResponse.of(강남역),
                StationResponse.of(역삼역),
                StationResponse.of(선릉역),
                StationResponse.of(선정릉역)
        );
    }

    @Test
    void 최단_경로_조회에_성공한다2() {
        // given
        given(stationRepository.findAll()).willReturn(List.of(강남역, 역삼역, 선릉역, 선정릉역, 강남구청역, 학동역, 논현역, 런던역, 파리역));
        given(sectionRepository.findAll()).willReturn(구간들);

        // when
        PathResponse result = pathService.findPath(런던역.getId(), 파리역.getId());

        // then
        assertThat(result.getDistance()).isEqualTo(10000);
        assertThat(result.getStations()).containsAnyOf(
                StationResponse.of(런던역),
                StationResponse.of(파리역)
        );
    }

    private static Station 역_생성(String name) {
        return FixtureUtil.getBuilder(Station.class)
                .set(javaGetter(Station::getName), name)
                .sample();
    }

    private static Section 구간_생성(Long upStationId, Long downStationId, int distance) {
        return FixtureUtil.getBuilder(Section.class)
                .set(javaGetter(Section::getUpStationId), upStationId)
                .set(javaGetter(Section::getDownStationId), downStationId)
                .set(javaGetter(Section::getDistance), distance)
                .sample();
    }
}