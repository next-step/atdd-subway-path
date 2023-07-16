package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.line.Section;
import nextstep.subway.line.SectionRepository;
import nextstep.subway.line.SectionRequest;
import nextstep.subway.line.UpdateLineRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.Assertions;
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
    private SectionRepository sectionRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("노선에 section을 추가한다")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = new Line("신분당선", "#D31145", new ArrayList<>());
        lineRepository.save(line);
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 1);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(
                line.getSections().stream()
                        .anyMatch(section -> section.containsStation(upStation) && section.containsStation(downStation))
        ).isTrue();
    }

    @DisplayName("노선을 저장한다")
    @Test
    void saveLine() {
        // given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));
        LineRequest lineRequest = new LineRequest("신분당선", "#D31145", upStation.getId(), downStation.getId(), 1);

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        Line savedLine = lineRepository.getReferenceById(lineResponse.getId());
        Assertions.assertAll(
                () -> assertThat(savedLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(savedLine.getColor()).isEqualTo("#D31145"),
                () -> assertThat(
                        savedLine.getSections().stream()
                                .anyMatch(section -> section.containsStation(upStation) && section.containsStation(
                                        downStation))
                ).isTrue());
    }

    @DisplayName("모든 노선을 보낸다")
    @Test
    void showLines() {
        // given
        Station gangnamStation = stationRepository.save(new Station("강남역"));
        Station yangjaeStation = stationRepository.save(new Station("양재역"));
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Section gangnamToYangjae = sectionRepository.save(new Section(null, gangnamStation, yangjaeStation, 1));
        Section yangjaeToPangyo = sectionRepository.save(new Section(null, yangjaeStation, pangyoStation, 2));
        Section pangyoToYangjae = sectionRepository.save(new Section(null, pangyoStation, yangjaeStation, 2));
        Section yangjaeToGangnam = sectionRepository.save(new Section(null, yangjaeStation, gangnamStation, 1));

        Line shinundangLine = lineRepository.save(
                new Line("신분당선", "#D31145", List.of(gangnamToYangjae, yangjaeToPangyo)));
        Line shinshinundangLine = lineRepository.save(
                new Line("신신분당선", "#D31146", List.of(pangyoToYangjae, yangjaeToGangnam)));

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        Assertions.assertAll(
                () -> assertThat(lineResponses).contains(
                        LineResponse.from(shinshinundangLine), LineResponse.from(shinundangLine))
        );
    }

    @DisplayName("아이디로 노선 조회")
    @Test
    void searchById() {
        // given
        Station gangnamStation = stationRepository.save(new Station("강남역"));
        Station yangjaeStation = stationRepository.save(new Station("양재역"));
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Section gangnamToYangjae = sectionRepository.save(new Section(null, gangnamStation, yangjaeStation, 1));
        Section yangjaeToPangyo = sectionRepository.save(new Section(null, yangjaeStation, pangyoStation, 2));

        Line line = lineRepository.save(
                new Line("신분당선", "#D31145", List.of(gangnamToYangjae, yangjaeToPangyo)));

        // when
        LineResponse lineResponse = lineService.searchById(line.getId());

        // then
        assertThat(lineResponse).isEqualTo(LineResponse.from(line));
    }

    @DisplayName("line 정보를 업데이트 한다")
    @Test
    void update() {
        // given
        Station gangnamStation = stationRepository.save(new Station("강남역"));
        Station yangjaeStation = stationRepository.save(new Station("양재역"));
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Section gangnamToYangjae = sectionRepository.save(new Section(null, gangnamStation, yangjaeStation, 1));
        Section yangjaeToPangyo = sectionRepository.save(new Section(null, yangjaeStation, pangyoStation, 2));
        Line line = lineRepository.save(
                new Line("신분당선", "#D31145", List.of(gangnamToYangjae, yangjaeToPangyo)));
        UpdateLineRequest updateLineRequest = new UpdateLineRequest("분당선", "#D31146");

        // when
        lineService.update(line.getId(), updateLineRequest);

        // then
        Line savedLine = lineRepository.findById(line.getId()).orElseThrow();
        Assertions.assertAll(
                () -> assertThat(savedLine.getName()).isEqualTo("분당선"),
                () -> assertThat(savedLine.getColor()).isEqualTo("#D31146")
        );
    }

    @DisplayName("id로 line를 삭제한다")
    @Test
    void deleteById() {
        // given
        Station gangnamStation = stationRepository.save(new Station("강남역"));
        Station yangjaeStation = stationRepository.save(new Station("양재역"));
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Section gangnamToYangjae = sectionRepository.save(new Section(null, gangnamStation, yangjaeStation, 1));
        Section yangjaeToPangyo = sectionRepository.save(new Section(null, yangjaeStation, pangyoStation, 2));
        Line line = lineRepository.save(
                new Line("신분당선", "#D31145", List.of(gangnamToYangjae, yangjaeToPangyo)));

        // when
        lineService.deleteById(line.getId());

        // then
        assertThatThrownBy(() -> lineRepository.findById(line.getId()).orElseThrow(LineNotFoundException::new))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("최 하행 스테이션으로 마지막 세션을 삭제한다")
    @Test
    void deleteSection() {
        // given
        Station gangnamStation = stationRepository.save(new Station("강남역"));
        Station yangjaeStation = stationRepository.save(new Station("양재역"));
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Section gangnamToYangjae = sectionRepository.save(new Section(null, gangnamStation, yangjaeStation, 1));
        Section yangjaeToPangyo = sectionRepository.save(new Section(null, yangjaeStation, pangyoStation, 2));
        List<Section> sections = new ArrayList<>();
        sections.add(gangnamToYangjae);
        sections.add(yangjaeToPangyo);
        Line line = lineRepository.save(
                new Line("신분당선", "#D31145", sections));

        // when
        lineService.deleteSection(line.getId(), pangyoStation.getId());

        // then
        Line savedLine = lineRepository.findById(line.getId()).orElseThrow(LineNotFoundException::new);
        Assertions.assertAll(
                () -> assertThat(savedLine.getSections().size()).isEqualTo(1),
                () -> assertThat(
                        savedLine.getSections().stream()
                                .anyMatch(section -> section.containsStation(yangjaeStation) && section.containsStation(
                                        pangyoStation))

                ).isFalse()
        );
    }

}
