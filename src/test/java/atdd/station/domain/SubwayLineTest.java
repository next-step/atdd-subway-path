package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static atdd.station.fixture.StationFixture.KANGNAM_AND_YUCKSAM_STATIONS;
import static atdd.station.fixture.SubwayLineFixture.SECOND_SUBWAY_LINE_NAME;
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
    public void addStationsInSubway() {
        //given

        SubwayLine subwayLine = new SubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        SubwayLine updatedSubwayLine = subwayLine.addStations(KANGNAM_AND_YUCKSAM_STATIONS);

        //then
        assertThat(updatedSubwayLine.getStations().size()).isGreaterThan(2);
    }

}
