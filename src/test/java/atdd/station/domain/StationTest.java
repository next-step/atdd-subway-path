package atdd.station.domain;

import org.junit.jupiter.api.Test;

import static atdd.station.fixture.StationFixture.KANGNAM_STATION_NAME;
import static org.assertj.core.api.Assertions.assertThat;

public class StationTest {

    @Test
    public void Station_삭제시_deleted_가_false_로_되는지() {
        //given
        Station station = new Station(KANGNAM_STATION_NAME);

        //when
        station.deleteStation();

        //then
        assertThat(station.isDeleted()).isTrue();
    }
}
