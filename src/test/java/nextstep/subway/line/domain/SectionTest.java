package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @ParameterizedTest
    @CsvSource(value = {"대방역:true", "신길역:true", "노량진역:false"}, delimiter = ':')
    public void containTesT(String stationName, boolean expected){
        //Given
        Section section = new Section(new Line("1호선", "블루"), new Station("대방역"), new Station("신길역"), 10);

        //When
        boolean result = section.containsStation(new Station(stationName));

        //Then
        assertThat(expected).isEqualTo(result);
    }

    @Test
    public void distanceAdjust(){
        //Given
        Section section = new Section(new Line("1호선", "블루"), new Station("대방역"), new Station("신길역"), 10);
//        Section section = new Section(new Line("1호선", "블루"), new Station("대방역"), new Station("신길역"), 10);

    }
}
