package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.exception.AlreadyExistStation;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 영등포역;
    private Station 신길역;
    private Station 대방역;
    private Station 노량진역;
    private Line line;

    @BeforeEach
    public void setUp() {
        영등포역 = stationRepository.save(new Station("영등포역"));
        신길역 = stationRepository.save(new Station("신길역"));
        대방역 = stationRepository.save(new Station("대방역"));
        노량진역 = stationRepository.save(new Station("노량진역"));
        line = lineRepository.save(new Line("신분당선", "red"));
    }

    @DisplayName("지하철 노선에 처음으로 구간을 등록한다.")
    @Test
    void addSection() {
        //When
        lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 신길역.getId(), 5));

        //Then
        Line expected = lineService.findLineById(line.getId());

        assertAll(
                () -> assertThat(expected.getSections()).hasSize(1),
                () -> assertThat(expected.getStations()).containsExactlyElementsOf(Arrays.asList(영등포역, 신길역))
        );
    }

    @DisplayName("새로운 구간을 추가한다. 추가된 상행역은 기존의 하행역과 동일하다.")
    @ParameterizedTest
    @CsvSource(value = {"3:5"}, delimiter = ':')
    public void addSection_case2(int distance1, int distance2) {
        //Given
        lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 신길역.getId(), distance1));

        //When
        lineService.addSection(line.getId(), new SectionRequest(신길역.getId(), 대방역.getId(), distance2));

        //Then
        Line expected = lineRepository.findById(line.getId()).get();
        assertAll(
                () -> assertThat(expected.getSections()).hasSize(2),
                () -> assertThat(expected.getStations()).containsExactlyElementsOf(Arrays.asList(영등포역, 신길역, 대방역)),
                () -> assertThat(expected.countTotalDistance()).isEqualTo(distance1 + distance2)
        );
    }

    @DisplayName("새로운 구간을 추가한다. 새로운 역은 상행 종점으로 등록된다.")
    @ParameterizedTest
    @CsvSource(value = {"10:5"}, delimiter = ':')
    public void addSection_case3(int distance1, int distance2) {
        //Given
        lineService.addSection(line.getId(), new SectionRequest(신길역.getId(), 노량진역.getId(), distance1));

        //When
        lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 신길역.getId(), distance2));

        //Then
        Line expected = lineRepository.findById(line.getId()).get();
        assertAll(
                () -> assertThat(expected.getSections()).hasSize(2),
                () -> assertThat(expected.getStations()).containsExactlyElementsOf(Arrays.asList(영등포역, 신길역, 노량진역)),
                () -> assertThat(expected.countTotalDistance()).isEqualTo(distance1 + distance2)
        );
    }

    @DisplayName("새로운 구간을 추가한다. 역 사이에 새로운 역을 등록한다. (상행역이 동일한 케이스)")
    @ParameterizedTest
    @CsvSource(value = {"7:5"}, delimiter = ':')
    public void addSection_case4(int distance1, int distance2) {
        //Given
        lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 대방역.getId(), distance1));

        //When
        lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 신길역.getId(), distance2));

        //Then
        Line expected = lineRepository.findById(line.getId()).get();
        assertAll(
                () -> assertThat(expected.getSections()).hasSize(2),
                () -> assertThat(expected.getStations()).containsExactlyElementsOf(Arrays.asList(영등포역, 신길역, 대방역)),
                () -> assertThat(expected.countTotalDistance()).isEqualTo(distance1)
        );
    }

    @DisplayName("새로운 구간을 추가한다. 역 사이에 새로운 역을 등록한다. (하행역 동일한 케이스)")
    @ParameterizedTest
    @CsvSource(value = {"7:5"}, delimiter = ':')
    public void addSection_case5(int distance1, int distance2) {
        //Given
        lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 노량진역.getId(), distance1));

        //When
        lineService.addSection(line.getId(), new SectionRequest(대방역.getId(), 노량진역.getId(), distance2));

        //Then
        Line expected = lineRepository.findById(line.getId()).get();
        assertAll(
                () -> assertThat(expected.getSections()).hasSize(2),
                () -> assertThat(expected.getStations()).containsExactlyElementsOf(Arrays.asList(영등포역, 대방역, 노량진역)),
                () -> assertThat(expected.countTotalDistance()).isEqualTo(distance1)
        );
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없음")
    @ParameterizedTest
    @CsvSource(value = {"7:7", "5:12"}, delimiter = ':')
    public void addSectionMoreThanDistanceException(int distance1, int distance2) {
        assertThatThrownBy(() -> {
            lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 노량진역.getId(), distance1));
            lineService.addSection(line.getId(), new SectionRequest(대방역.getId(), 노량진역.getId(), distance2));
        }).isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @ParameterizedTest
    @CsvSource(value = {"7:7", "5:12"}, delimiter = ':')
    public void addSectionAlreadyStations(int distance1, int distance2) {
        assertThatThrownBy(() -> {
            lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 노량진역.getId(), distance1));
            lineService.addSection(line.getId(), new SectionRequest(영등포역.getId(), 노량진역.getId(), distance2));
        }).isInstanceOf(AlreadyExistStation.class);
    }
}
