package nextstep.subway.unit.domain;

import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.error.ErrorCode;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Line line;
    private Section section;
    private Station 서초역;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    void setFixtures() {
        setUp();
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionInLast() {
        // when
        line.addSection(new Section(line, 역삼역, 삼성역, 5));

        // then
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInFirst() {
        // when
        line.addSection(new Section(line, 서초역, 교대역, 5));

        // then
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInBetween() {
        // when
        line.addSection(new Section(line, 교대역, 강남역, 3));

        // then
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        line.addSection(new Section(line, 역삼역, 삼성역, 5));
        line.addSection(new Section(line, 서초역, 교대역, 5));
        line.addSection(new Section(line, 교대역, 강남역, 3));

        // when
        LineResponse lineResponse = LineResponse.of(line);
        List<StationResponse> stations = lineResponse.getStations();

        // then
        StationResponse lastStation = stations.get(stations.size() - 1);
        assertThat(lastStation.getName()).isEqualTo(삼성역.getName());
    }

    @DisplayName("구간 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        line.addSection(new Section(line, 역삼역, 삼성역, 5));
        line.addSection(new Section(line, 서초역, 교대역, 5));
        line.addSection(new Section(line, 교대역, 강남역, 3));

        line.removeSection(삼성역.getId());
        assertThat(line.getSections().getSections()).hasSize(3);
    }

    @DisplayName("구간 목록에 존재하지 않는 역 삭제시 실패")
    @Test
    void removeNotExistSectionIsFailed() {
        // given
        line.addSection(new Section(line, 역삼역, 삼성역, 5));
        line.addSection(new Section(line, 서초역, 교대역, 5));
        line.addSection(new Section(line, 교대역, 강남역, 3));

        // when then
        assertThatThrownBy(() -> {
            line.removeSection(Long.MAX_VALUE);
        }).isInstanceOf(NotFoundStationException.class);
    }

    @DisplayName("구간 목록에서 중간역 삭제")
    @Test
    void removeMiddleSection() {
        // given
        // 서초-교대-강남-역삼-삼성
        line.addSection(new Section(line, 역삼역, 삼성역, 5));
        line.addSection(new Section(line, 서초역, 교대역, 5));
        line.addSection(new Section(line, 교대역, 강남역, 3));

        // when
        line.removeSection(교대역.getId());
        Sections sections = line.getSections();

        // then
        assertThat(sections.getSections()).hasSize(3);
        Section firstSection = sections.findSectionHasUpStationEndPoint();
        assertThat(firstSection.getDownStation()).isEqualTo(강남역);
        assertThat(firstSection.getDistance()).isEqualTo(8);
    }

    @DisplayName("구간 목록이 1개일때 삭제시 실패")
    @Test
    void ifThereIsOneListOfIntervalsTheDeletionFails() {
        // when then
        assertThatThrownBy(() -> {
            line.removeSection(교대역.getId());
        }).isInstanceOf(ValidationException.class)
          .hasMessage(ErrorCode.SECTION_MINIMUM_SIZE_ERROR.getMessage());
    }

    private void setUp() {
        line = new Line("2호선", "bg-red-600");
        서초역 = new Station("서초역");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        section = new Section(line, 교대역, 역삼역, 10);
        line.addSection(section);

        ReflectionTestUtils.setField(서초역, "id", 1L);
        ReflectionTestUtils.setField(교대역, "id", 2L);
        ReflectionTestUtils.setField(강남역, "id", 3L);
        ReflectionTestUtils.setField(역삼역, "id", 4L);
        ReflectionTestUtils.setField(삼성역, "id", 5L);
    }
}
