package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nextstep.subway.common.exception.InvalidSectionException;
import nextstep.subway.common.exception.NoResourceException;
import nextstep.subway.line.acceptance.LineColor;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LineServiceTest {

  @Autowired
  private LineService lineService;

  @Autowired
  private StationService stationService;

  @Autowired
  private DatabaseCleanup databaseCleanup;

  LineResponse 신분당선;
  StationResponse 광교역;
  StationResponse 광교중앙역;

  @BeforeEach
  void init() {
    광교역 = stationService.saveStation(new StationRequest("광교역"));
    광교중앙역 = stationService.saveStation(new StationRequest("광교중앙역"));
    //when
    신분당선 = lineService
        .saveLine(new LineRequest("신분당선", LineColor.RED.toString(), 광교역.getId(), 광교중앙역.getId(), 5));
  }

  @AfterEach
  void clean() {
    databaseCleanup.execute();
  }

  @DisplayName("지하철 노선을 생성한다")
  @Test
  void saveLine() {
    //then
    assertThat(신분당선.getStations()).hasSize(2);
  }

  @DisplayName("지하철 전체노선을 조회한다")
  @Test
  void getLines() {
    //when
    List<LineResponse> lineResponses = lineService.getLines();
    //then
    LineResponse lineResponse = lineResponses.stream().findFirst().get();
    assertThat(lineResponse.getName()).isEqualTo("신분당선");
  }


  @DisplayName("지하철 노선을 조회한다")
  @Test
  void findLineById() {
    //when
    LineResponse lineResponse = lineService.findLineById(신분당선.getId());

    //then
    assertAll(
        () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
        () -> assertThat(lineResponse.getStations()).hasSize(2)
    );

  }


  @DisplayName("등록되지 않은 지하철 노선이면 NoResourceException throw")
  @Test
  void findLineByIdWithException() {
    //then
    assertThrows(NoResourceException.class, () -> {
      lineService.findLineById(2L);
    });
  }

  @DisplayName("등록된 지하철 노선을 수정한다")
  @Test
  void modifyLine() {
    LineResponse lineResponse = lineService
        .modifyLine(신분당선.getId(),
            new LineRequest("신분당선", LineColor.YELLOW.toString(), 광교역.getId(), 광교중앙역.getId(), 5));
    assertThat(lineResponse.getColor()).isEqualTo(LineColor.YELLOW.toString());
  }

  @DisplayName("지하철 노선을 제거한다")
  @Test
  void removeLine() {
    //given
    lineService.removeLine(신분당선.getId());

    //then
    assertThrows(NoResourceException.class, () -> {
      lineService.findLineById(신분당선.getId());
    });
  }

  @DisplayName("지하철 노선에 새로운 구간을 추가한다")
  @Test
  void addSection() {
    //given
    StationResponse 상현역 = stationService.saveStation(new StationRequest("상현역"));
    //when
    lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), 5));
    LineResponse lineResponse = lineService.findLineById(신분당선.getId());
    //then
    assertAll(
        () -> assertThat(lineResponse.getStations()).hasSize(3),
        () -> assertThat(lineResponse.getStations()).extracting(StationResponse::getName)
            .containsExactly("광교역", "광교중앙역", "상현역")
    );
  }

  @DisplayName("등록하는 구간의 상행역이 노선의 종점이아닌경우 구간 등록할 수 없다.")
  @Test
  void addSectionWithInvalidUpStation() {
    //given
    StationResponse 상현역 = stationService.saveStation(new StationRequest("상현역"));

    //then
    assertThrows(InvalidSectionException.class, () -> {
      lineService.addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 상현역.getId(), 5));
    });
  }

  @DisplayName("등록하는 구간의 하행역이 노선에 이미 등록되어있으면 구간을 등록할 수 없다.")
  @Test
  void addSectionWithInvalidDownStationSection() {
    //given
    StationResponse 상현역 = stationService.saveStation(new StationRequest("상현역"));
    lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), 5));

    //then
    assertThrows(InvalidSectionException.class, () -> {
      lineService.addSection(신분당선.getId(), new SectionRequest(상현역.getId(), 광교중앙역.getId(), 5));
    });

  }

  @DisplayName("노선에서 구간이 1개남아있으면 역을 삭제하지못한다")
  @Test
  void removeSectionIncludedOneSection() {
    assertThrows(InvalidSectionException.class, () -> {
      lineService.removeSection(신분당선.getId(), 광교중앙역.getId());
    });
  }

  @DisplayName("노선에서 삭제하려는 역이 종점이 아니면 삭제하지 못한다")
  @Test
  void removeSectionWithoutLastStation() {

    assertThrows(InvalidSectionException.class, () -> {
      lineService.removeSection(신분당선.getId(), 광교역.getId());
    });
  }

  @DisplayName("노선에서 구간을 삭제한다.")
  @Test
  void removeSection() {
    //given
    StationResponse 상현역 = stationService.saveStation(new StationRequest("상현역"));
    lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), 5));

    //when
    lineService.removeSection(신분당선.getId(), 상현역.getId());
    LineResponse lineResponse = lineService.findLineById(신분당선.getId());

    //then
    assertThat(lineResponse.getStations()).extracting(StationResponse::getName)
        .containsExactly("광교역", "광교중앙역");
  }

}
