package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 삼성역;
    Station 서울역;

    Line 이호선;
    Line 일호선;

    @BeforeEach
    void setup() {
        강남역 = stationRepository.save(new Station(1L, "강남역"));
        역삼역 = stationRepository.save(new Station(2L, "역삼역"));
        선릉역 = stationRepository.save(new Station(3l, "선릉역"));
        삼성역 = stationRepository.save(new Station(4L, "삼성역"));
        서울역 = stationRepository.save(new Station(5L, "서울역"));

        일호선 = lineRepository.save(new Line("1호선", "bg-blue-600"));
        이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
    }

    @Test
    @DisplayName("지하철 구간 등록합니다.")
    void addSection() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        Section section = Section.builder()
            .line(이호선)
            .upStation(강남역)
            .downStation(역삼역)
            .distance(6)
            .build();

        assertAll(() -> {
            assertThat(이호선.getSectionList()).contains(section);
            assertThat(이호선.getSectionList()).hasSize(1);
        });

    }

    @Test
    @DisplayName("지하철 구간목록 조회합니다.")
    void showSection() {
        List<LineResponse> 비교값 = List.of(
            LineResponse.builder().id(일호선.getId()).name(일호선.getName()).color(일호선.getColor())
                .stations(List.of()).build(),
            LineResponse.builder().id(이호선.getId()).name(이호선.getName()).color(이호선.getColor())
                .stations(List.of()).build()
        );

        List<LineResponse> 노선목록 = lineService.showLines();

        assertAll(() -> {
            assertThat(노선목록).hasSize(2);
            assertThat(노선목록).isEqualTo(비교값);
        });
    }

    @Test
    @DisplayName("지하철 노선 조회합니다.")
    void findSection() {
        LineResponse 일호선_반환값 = LineResponse.builder()
            .id(일호선.getId())
            .name(일호선.getName())
            .color(일호선.getColor())
            .stations(List.of())
            .build();

        LineResponse 일호선_조회한값 = lineService.findById(일호선.getId());

        assertThat(일호선_조회한값).isEqualTo(일호선_반환값);
    }

    @Test
    @DisplayName("지하철 노선 수정합니다")
    void updateSection() {
        lineService.updateLine(이호선.getId(),
            new LineRequest("1호선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 6));

        assertAll(() -> {
            assertThat(이호선.getName()).isEqualTo("1호선");
            assertThat(이호선.getColor()).isEqualTo("bg-red-600");
        });
    }

    @Test
    @DisplayName("지하철 노선 삭제합니다")
    void removeLine() {
        lineService.deleteLine(이호선.getId());

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.findById(이호선.getId());
        });
    }

    @Test
    @DisplayName("구간이 하나일때 삭제를 시도할 시 에러를 반환합니다.")
    void removeSectionException() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.deleteSection(이호선.getId(), 역삼역.getId());
            })
            .withMessage("구간이 하나일때는 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("상행 종점 지하철역을 삭제합니다.")
    void removeFirstStation() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 4));

        lineService.deleteSection(이호선.getId(), 강남역.getId());

        assertThat(이호선.getStations()).containsExactly(역삼역, 선릉역);
    }

    @Test
    @DisplayName("하행 종점 지하철역을 삭제합니다.")
    void removeLastStation() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 4));

        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역);
    }

    @Test
    @DisplayName("지하철 구간 사이에 있는 지하철역을 삭제합니다.")
    void remoteBetweenStation() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 4));
        lineService.addSection(이호선.getId(), new SectionRequest(선릉역.getId(), 삼성역.getId(), 10));

        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 삼성역);
    }

    @Test
    @DisplayName("지하철역이 존재하지 않는데 삭제를 시도할 경우 에러를 반환합니다.")
    void isNotExistsStation() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 4));

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.deleteSection(이호선.getId(), 삼성역.getId());
            })
            .withMessage("존재하지 않는 지하철역이라 삭제할 수가 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선에 상행선 추가합니다.")
    void insertUpSection() {
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
        });
    }

    @Test
    @DisplayName("지하철 상행선 기준으로 구간을 사이에 추가합니다.")
    void insertBetweenUpSection() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), 10));
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
        });
    }

    @Test
    @DisplayName("자허철 노선에 하행선 추가 합니다.")
    void insertDownStation() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 6));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
        });
    }

    @Test
    @DisplayName("지하철 노선에 하행선 구간을 사이에 추가합니다.")
    void insertBetweenDownStation() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), 10));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(3);
            assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
        });
    }

    @Test
    @DisplayName("지하철 노선에 중복되는 노선을 넣었을 경우 에러를 반환합니다.")
    void existsSectionException() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), 10));

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), 10));
            })
            .withMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없을 때 에러를 반환합니다.")
    void notContainsUpStationOrDownStation() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.addSection(이호선.getId(), new SectionRequest(서울역.getId(), 선릉역.getId(), 200));
            })
            .withMessage("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선을 상행선과 하행선 사이에 추가하는데 길이가 초과하였을 경우 에러를 반환합니다.")
    void addSectionLengthException() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), 6));

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 200));
            })
            .withMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선 중간에도 구간이 추가가 되는지 테스트 합니다.")
    void insertBetweenSection() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 삼성역.getId(), 20));
        lineService.addSection(이호선.getId(), new SectionRequest(선릉역.getId(), 삼성역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 4));

        assertAll(() -> {
            assertThat(이호선.getStations()).hasSize(4);
            assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역, 삼성역);
        });
    }

}
