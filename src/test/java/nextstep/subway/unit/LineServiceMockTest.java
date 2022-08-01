package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 삼성역;
    Station 서울역;

    Line 일호선;
    Line 이호선;

    @BeforeEach
    void setup() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        삼성역 = new Station(4L, "삼성역");
        서울역 = new Station(5L, "서울역");

        일호선 = new Line("1호선", "bg-blue-600");
        이호선 = new Line("2호선", "bg-green-600");
    }

    @Test
    @DisplayName("지하철 구간을 등록합니다.")
    void addSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);

        lineService.addSection(1L, new SectionRequest(1L, 2L, 6));

        LineResponse 이호선_반환값 = lineService.findById(1L);
        LineResponse 비교값 = createLineResponse("2호선", "bg-green-600", List.of(강남역, 역삼역));

        assertThat(이호선_반환값).isEqualTo(비교값);
    }

    @Test
    @DisplayName("지하철 노선 목록 조회 합니다")
    void showSection() {
        given(lineRepository.findAll()).willReturn(List.of(
            일호선,
            이호선
        ));

        List<LineResponse> 노선목록 = lineService.showLines();
        List<LineResponse> 비교값 = List.of(createLineResponse("1호선", "bg-blue-600", List.of()),
            createLineResponse("2호선", "bg-green-600", List.of()));

        assertThat(노선목록).isEqualTo(비교값);
    }

    @Test
    @DisplayName("지하철 노선을 조회합니다.")
    void findLine() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(일호선));

        LineResponse 일호선_반환값 = lineService.findById(1L);

        assertThat(일호선_반환값).isEqualTo(createLineResponse("1호선", "bg-blue-600", List.of()));
    }

    @Test
    @DisplayName("지하철 노선을 수정 합니다.")
    void updateSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(일호선));

        lineService.updateLine(1L, new LineRequest("2호선", "bg-green-600", 1L, 2L, 6));

        LineResponse 일호선_반환값 = lineService.findById(1L);

        assertThat(일호선_반환값).isEqualTo(createLineResponse("2호선", "bg-green-600", List.of()));
    }

    @Test
    @DisplayName("지하철 노선을 삭제 합니다.")
    void removeLine() {
        given(lineRepository.save(any(Line.class))).willReturn(new Line("1호선", "bg-blue-500"));
        given(lineRepository.findById(1L)).willReturn(Optional.empty());
        given(stationService.findById(강남역.getId())).willReturn(강남역);
        given(stationService.findById(역삼역.getId())).willReturn(역삼역);

        lineService.saveLine(new LineRequest("1호선", "bg-blue-500", 강남역.getId(), 역삼역.getId(), 6));
        lineService.deleteLine(1L);

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.findById(1L);
        });
    }

    @Test
    @DisplayName("구간이 하나일때 삭제를 시도할 시 에러를 반환합니다.")
    void removeSectionException() {
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(2L)).willReturn(역삼역);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.deleteSection(1L, 2L);
            })
            .withMessage("구간이 하나일때는 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("상행 종점 지하철 역을 삭제합니다.")
    void removeFirstStation() {
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 4));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);

        lineService.deleteSection(1L, 강남역.getId());

        assertThat(이호선.getStations()).containsExactly(역삼역, 선릉역);
    }

    @Test
    @DisplayName("하행 종점 지하철 역을 삭제합니다.")
    void removeLastStation() {
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 4));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(선릉역.getId())).willReturn(선릉역);

        lineService.deleteSection(1L, 선릉역.getId());

        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역);
    }

    @Test
    @DisplayName("지하철 노선중 중간 지하철 역을 삭제하는 테스트를 합니다.")
    void removeBetweenStation() {
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 4));
        이호선.addSection(new Section(이호선, 선릉역, 삼성역, 10));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(선릉역.getId())).willReturn(선릉역);

        lineService.deleteSection(1L, 선릉역.getId());

        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 삼성역);
    }

    @Test
    @DisplayName("지하철 노선에 존재하지 않는 지하철역을 삭제할 때 에러를 반환합니다.")
    void isNotExistsStationException() {
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 4));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(삼성역.getId())).willReturn(삼성역);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.deleteSection(1L, 삼성역.getId());
            })
            .withMessage("존재하지 않는 지하철역이라 삭제할 수가 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선에 상행선 추가합니다.")
    void insertUpSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);
        given(stationService.findById(3L)).willReturn(선릉역);

        lineService.addSection(1L, new SectionRequest(2L, 3L, 6));
        lineService.addSection(1L, new SectionRequest(1L, 2L, 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(new Station[]{강남역, 역삼역, 선릉역});
        });
    }

    @Test
    @DisplayName("지하철 상행선 기준으로 구간을 사이에 추가합니다.")
    void insertBetweenUpSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);
        given(stationService.findById(3L)).willReturn(선릉역);

        lineService.addSection(1L, new SectionRequest(1L, 3L, 10));
        lineService.addSection(1L, new SectionRequest(1L, 2L, 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(new Station[]{강남역, 역삼역, 선릉역});
        });
    }

    @Test
    @DisplayName("자허철 노선에 하행선 추가 합니다.")
    void insertDownStation() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);
        given(stationService.findById(3L)).willReturn(선릉역);

        lineService.addSection(1L, new SectionRequest(1L, 2L, 6));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(new Station[]{강남역, 역삼역, 선릉역});
        });
    }

    @Test
    @DisplayName("지하철 노선에 하행선 구간을 사이에 추가합니다.")
    void insertBetweenDownStation() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);
        given(stationService.findById(3L)).willReturn(선릉역);

        lineService.addSection(1L, new SectionRequest(1L, 3L, 10));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(new Station[]{강남역, 역삼역, 선릉역});
        });
    }

    @Test
    @DisplayName("지하철 노선에 중복되는 노선을 넣었을 경우 에러를 반환합니다.")
    void existsSectionException() {
        일호선.addSection(new Section(일호선, 강남역, 역삼역, 10));

        given(lineRepository.findById(1L)).willReturn(Optional.of(일호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.addSection(1L, new SectionRequest(강남역.getId(), 역삼역.getId(), 10));
            })
            .withMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없을 때 에러를 반환합니다.")
    void notContainsUpStationOrDownStation() {
        일호선.addSection(new Section(일호선, 강남역, 역삼역, 10));

        given(lineRepository.findById(1L)).willReturn(Optional.of(일호선));
        given(stationService.findById(선릉역.getId())).willReturn(선릉역);
        given(stationService.findById(서울역.getId())).willReturn(서울역);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.addSection(1L, new SectionRequest(서울역.getId(), 선릉역.getId(), 200));
            })
            .withMessage("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선을 상행선과 하행선 사이에 추가하는데 길이가 초과하였을 경우 에러를 반환합니다.")
    void addSectionLengthException() {
        일호선.addSection(new Section(일호선, 강남역, 선릉역, 10));

        given(lineRepository.findById(1L)).willReturn(Optional.of(일호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.addSection(1L, new SectionRequest(강남역.getId(), 역삼역.getId(), 10));
            })
            .withMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선 중간에 구간을 추가하는 테스트를 합니다.")
    void addBetweenSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);
        given(stationService.findById(3L)).willReturn(선릉역);
        given(stationService.findById(4L)).willReturn(삼성역);

        lineService.addSection(1L, new SectionRequest(1L, 4L, 30));
        lineService.addSection(1L, new SectionRequest(3L, 4L, 10));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(4);
            assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역, 삼성역);
        });
    }


    private LineResponse createLineResponse(String name, String color, List<Station> stationName) {
        return LineResponse.builder()
            .name(name)
            .color(color)
            .stations(stationName.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList()))
            .build();
    }

}
