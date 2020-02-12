package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static atdd.station.fixture.StationFixture.KANGNAM_AND_YUCKSAM_STATIONS;
import static atdd.station.fixture.StationFixture.YUCKSAM_STATION_NAME;
import static atdd.station.fixture.SubwayLineFixture.SECOND_SUBWAY_LINE_NAME;
import static atdd.station.fixture.SubwayLineFixture.getSecondSubwayLineName;
import static org.assertj.core.api.Assertions.assertThat;

public class SubwayLineTest {

    @DisplayName("Station_삭제시_deleted_가_false_로_되는지")
    @Test
    public void deleteSubwayLineSuccessTest() {
        //given

        SubwayLine subwayLine = new SubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        subwayLine.deleteSubwayLine();

        //then
        assertThat(subwayLine.isDeleted()).isTrue();
    }

    @DisplayName("Subway_에_Stations_를_추가가_성공하는지")
    @Test
    public void addStationsInSubwayTest() {
        //given

        SubwayLine subwayLine = new SubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        SubwayLine updatedSubwayLine = subwayLine.updateSubwayByStations(KANGNAM_AND_YUCKSAM_STATIONS);

        //then
        assertThat(updatedSubwayLine.getStations().size()).isEqualTo(2);
    }

    @DisplayName("Stations_로_해당_subwayLine_에_추가된_것이_만들어지는지")
    @Test
    public void makeSubwayByStationSuccessTest() {
        //given

        SubwayLine subwayLine = new SubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        List<Subway> madeSubways = subwayLine.makeSubwaysByStations(KANGNAM_AND_YUCKSAM_STATIONS);

        //then
        assertThat(madeSubways.size()).isEqualTo(2);
    }

    @DisplayName("역삼역_의_이름으로_삭제가_가능한지")
    @Test
    public void deleteStationByNameTest() {
        //given
        SubwayLine subwayLine = getSecondSubwayLineName();

        //when
        subwayLine.deleteStationByName(YUCKSAM_STATION_NAME);

        //then
        assertThat(subwayLine.getStations().size()).isEqualTo(3);
    }

    @DisplayName("subwayLine_내의_stations_에서_이름으로_해당_역으로_가져오는지")
    @Test
    public void getStationByNameSuccessTest() {
        //given
        SubwayLine subwayLine = getSecondSubwayLineName();

        //when
        Station station = subwayLine.getStationByName(YUCKSAM_STATION_NAME);

        //then
        assertThat(station.getName()).isEqualTo(YUCKSAM_STATION_NAME);
    }

}
