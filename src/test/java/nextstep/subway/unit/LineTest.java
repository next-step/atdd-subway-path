package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class LineTest {
    Line line;
    Station firstUpStation;
    Station firstDownStation;
    Section firstSection;

    Station newStation;
    Section newSection;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        firstUpStation = new Station("신림역");
        firstDownStation = new Station("봉천역");
        firstSection = new Section(line, firstUpStation, firstDownStation, 10);
        newStation = new Station("서울대입구역");
        newSection = new Section(line, firstDownStation, newStation, 10);

        setField(firstUpStation, "id", 1L);
        setField(firstDownStation, "id", 2L);
        setField(newStation, "id", 3L);
        setField(firstSection, "id", 1L);
        setField(newSection, "id", 2L);

        line.getSections().add(firstSection);
    }
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // when
        line.addSection(newSection);

        // then
        List<Long> ids = line.getSections().getSections().stream().map(Section::getId).collect(Collectors.toList());
        assertThat(ids).containsExactly(firstSection.getId(), newSection.getId());

    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        line.addSection(newSection);

        // when
        List<Station> stations = line.getStations();

        // then
        List<Long> ids = stations.stream().map(Station::getId).collect(Collectors.toList());
        assertThat(ids).containsExactly(firstUpStation.getId(), firstDownStation.getId(), newStation.getId());
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        line.addSection(newSection);

        // when
        line.removeSection(newStation.getId());

        // then
        List<Long> ids = line.getSections().getSections().stream().map(Section::getId).collect(Collectors.toList());
        assertThat(ids).containsExactly(firstSection.getId());
    }
}
