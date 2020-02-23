package atdd.line.domain;

import atdd.station.domain.Duration;
import atdd.station.domain.Station;
import atdd.station.domain.StationTest;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    private final String name = "name!!";
    private final TimeTable timeTable = new TimeTable(LocalTime.MIN, LocalTime.MAX);
    private final int intervalTime = 1;

    private final Station station1 = StationTest.create(4341L, "stationName1!!");
    private final Station station2 = StationTest.create(4342L, "stationName2!!");
    private final Station station3 = StationTest.create(4343L, "stationName3!!");
    private final Station station4 = StationTest.create(4344L, "stationName4!!");

    @Test
    void create() throws Exception {
        final Line line = Line.create(name, timeTable, intervalTime);

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getTimeTable()).isEqualTo(timeTable);
        assertThat(line.getIntervalTime()).isEqualTo(intervalTime);
    }

    @DisplayName("create - name 은 null 이거나 빈 값일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createByNullAndEmptyName(String nullAndEmptyName) {
        assertThatThrownBy(() -> Line.create(nullAndEmptyName, timeTable, intervalTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name 은 필수값 입니다.");
    }

    @DisplayName("create - timeTable 은 null 일 수 없다.")
    @Test
    void createByNullTimeTable() {
        final TimeTable nullTimeTable = null;

        assertThatThrownBy(() -> Line.create(name, nullTimeTable, intervalTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("timeTable 은 필수값 입니다.");
    }

    @DisplayName("create - timeTable 은 음수 일 수 없다.")
    @Test
    void createByNegativeIntervalTime() {
        final int negativeIntervalTime = -1;

        assertThatThrownBy(() -> Line.create(name, timeTable, negativeIntervalTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("intervalTime 은 음수일 수 없습니다.");
    }

    @DisplayName("addStation - 동일한 이름의 Station 추가시 에러")
    @Test
    void addSameNameStation() throws Exception {
        final Station station = Station.create("stationName!!");

        final Line line = Line.create(name, timeTable, intervalTime);

        line.addStation(station);
        assertThatThrownBy(() -> line.addStation(station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 역입니다. 역이름 : [stationName!!]");
    }

    @Test
    void getStations() {
        final Line line = Line.create(name, timeTable, intervalTime);
        line.addStation(station1);
        line.addStation(station2);


        final List<Station> stations = line.getStations();


        assertThat(stations).hasSize(2);
        assertThat(stations.get(0)).isEqualTo(station1);
        assertThat(stations.get(1)).isEqualTo(station2);
    }

    @DisplayName("isSameName - 입력받은 name 이 동일한 이름이면 true")
    @Test
    void isSameName() {
        final Line line = Line.create(name, timeTable, intervalTime);

        assertThat(line.isSameName(name)).isTrue();
        assertThat(line.isSameName(name + "not")).isFalse();
    }

    @DisplayName("isSameName - 입력받은 name 이 비어있거나 null 이면 false")
    @ParameterizedTest
    @NullAndEmptySource
    void isSameNameByNullAndEmptyName(String nullAndEmptyName) {
        final Line line = Line.create(name, timeTable, intervalTime);

        assertThat(line.isSameName(nullAndEmptyName)).isFalse();
    }

    @DisplayName("addStation - line 에 Station 최초로 추가한 station 이  startStation 이 된다.")
    @Test
    void addSection() throws Exception {
        final Duration duration = new Duration(LocalTime.MAX);
        final double distance = 1;

        final Line line = Line.create(name, timeTable, intervalTime);
        line.addStation(station1);
        line.addStation(station2);


        line.addSection(station1.getId(), station2.getId(), duration, distance);


        assertThat(line.getStartStation().get()).isEqualTo(station1);


        final Station nextStation1 = CollectionUtils.firstElement(station1.getSameLineNextStations(line));
        final Station nextStation2 = CollectionUtils.firstElement(station2.getSameLineNextStations(line));
        assertThat(nextStation1).isEqualTo(station2);
        assertThat(nextStation2).isEqualTo(station1);
    }

    @DisplayName("addStation - 새로운 역에 첫번째 역을 구간으로 추가하면 새로운역이 startStation 이 된다.")
    @Test
    void addSectionNewStationToFirstStation() throws Exception {
        final Duration duration = new Duration(LocalTime.MAX);
        final double distance = 1;

        final Line line = Line.create(name, timeTable, intervalTime);
        line.addStation(station1);
        line.addStation(station2);
        line.addStation(station3);


        line.addSection(station1.getId(), station2.getId(), duration, distance);
        line.addSection(station3.getId(), station1.getId(), duration, distance);

        assertThat(line.getStartStation().get()).isEqualTo(station3);
    }

    @DisplayName("getOrderedStations - 첫번째 역이 없으면 empty")
    @Test
    void getOrderedStationsNullStartStation() throws Exception {
        Line line = Line.create(name, timeTable, intervalTime);

        assertThat(line.getOrderedStations()).isEmpty();
    }

    @DisplayName("getOrderedStations - 구간 순서대로 역이 반환된다.")
    @Test
    void getOrderedStations() throws Exception {
        final Duration duration = new Duration(LocalTime.MAX);
        final double distance = 1;
        final Line line = Line.create(name, timeTable, intervalTime);
        final List<Station> expectedOrderedStation = Lists.list(station1, station3, station2, station4);

        line.addStation(station1);
        line.addStation(station4);
        line.addStation(station3);
        line.addStation(station2);

        line.addSection(station1.getId(), station3.getId(), duration, distance);
        line.addSection(station3.getId(), station2.getId(), duration, distance);
        line.addSection(station2.getId(), station4.getId(), duration, distance);

        final List<Station> orderedStations = line.getOrderedStations();

        assertThat(orderedStations).isEqualTo(expectedOrderedStation);
    }

    @DisplayName("deleteStation - 중간 역을 삭제하면 앞/뒤 역이 연결된다.")
    @Test
    void deleteStation() throws Exception {
        final List<Station> expectedOrderedStation = Lists.list(station1, station3);

        final Duration duration1 = new Duration(LocalTime.of(1, 1));
        final Duration duration2 = new Duration(LocalTime.of(2, 2));
        final Duration expectedDuration = duration1.add(duration2);

        final double distance1 = 1.5;
        final double distance2 = 2.5;
        final double expectedDistance = distance1 + distance2;

        final Line line = Line.create(name, timeTable, intervalTime);

        line.addStation(station1);
        line.addStation(station2);
        line.addStation(station3);

        line.addSection(station1.getId(), station2.getId(), duration1, distance1);
        line.addSection(station2.getId(), station3.getId(), duration2, distance2);

        
        line.deleteStation(station2.getId());


        final List<Station> stations = line.getStations();
        assertThat(Sets.newHashSet(stations)).isEqualTo(Sets.newHashSet(expectedOrderedStation));

        final List<Station> orderedStations = line.getOrderedStations();
        assertThat(orderedStations).isEqualTo(expectedOrderedStation);

        final Station result1 = orderedStations.get(0);
        final Station result2 = orderedStations.get(1);

        assertThat(result1.getDuration(line, result2)).isEqualTo(expectedDuration);
        assertThat(result2.getDuration(line, result1)).isEqualTo(expectedDuration);

        assertThat(result1.getDistance(line, result2)).isEqualTo(expectedDistance);
        assertThat(result2.getDistance(line, result1)).isEqualTo(expectedDistance);


        final Set<Station> sameLineStations = station2.getSameLineNextStations(line);
        assertThat(sameLineStations).isEmpty();
    }

    @DisplayName("deleteStation - 첫번째 역을 삭제하면 두번째 역이 첫번째 역이 된다.")
    @Test
    void deleteStationWithStartStation() throws Exception {
        final List<Station> expectedOrderedStation = Lists.list(station2, station3);

        final Duration duration1 = new Duration(LocalTime.of(1, 1));
        final Duration duration2 = new Duration(LocalTime.of(2, 2));

        final double distance1 = 1.5;
        final double distance2 = 2.5;

        final Line line = Line.create(name, timeTable, intervalTime);

        line.addStation(station1);
        line.addStation(station2);
        line.addStation(station3);

        line.addSection(station1.getId(), station2.getId(), duration1, distance1);
        line.addSection(station2.getId(), station3.getId(), duration2, distance2);


        line.deleteStation(station1.getId());


        final List<Station> stations = line.getStations();
        assertThat(Sets.newHashSet(stations)).isEqualTo(Sets.newHashSet(expectedOrderedStation));

        final List<Station> orderedStations = line.getOrderedStations();
        assertThat(orderedStations).isEqualTo(expectedOrderedStation);
    }


    @DisplayName("delete - 삭제 대상역의 동일한 노선에 연결된 역의 갯수가 3개 이상이면 에러")
    @Test
    void deleteStationNotDeletableSize() throws Exception {
        final Duration duration = new Duration(LocalTime.MAX);
        final double distance = 1;

        final Line line = Line.create(name, timeTable, intervalTime);

        line.addStation(station1);
        line.addStation(station2);
        line.addStation(station3);
        line.addStation(station4);

        line.addSection(station1.getId(), station2.getId(), duration, distance);
        line.addSection(station2.getId(), station3.getId(), duration, distance);
        line.addSection(station2.getId(), station4.getId(), duration, distance);

        assertThatThrownBy(() -> line.deleteStation(station2.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("3개 역이 연결되어 있습니다. 삭제는 2개일때만 가능합니다.");
    }

    public static Line create(Long id, String name, TimeTable timeTable, int intervalTime) {
        Line line = new Line();
        ReflectionTestUtils.setField(line, "id", id);
        ReflectionTestUtils.setField(line, "name", name);
        ReflectionTestUtils.setField(line, "timeTable", timeTable);
        ReflectionTestUtils.setField(line, "intervalTime", intervalTime);
        return line;
    }

    public static void addStations(Line line, Station... station) {
        for (Station target : station) {
            line.addStation(target);
        }

    }

}