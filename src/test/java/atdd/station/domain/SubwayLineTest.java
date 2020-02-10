package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubwayLineTest {

    @DisplayName("Station_삭제시_deleted_가_false_로_되는지")
    @Test
    public void deleteSubwayLineSuccessTest() {
        //given

        SubwayLine subwayLine = new SubwayLine("2호선");

        //when
        subwayLine.deleteSubwayLine();

        //then
        assertThat(subwayLine.isDeleted()).isTrue();
    }
}
