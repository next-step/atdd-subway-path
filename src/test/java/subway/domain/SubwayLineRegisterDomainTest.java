package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;

import java.math.BigDecimal;

/**
 * 지하철 노선 생성에 대한 도메인 단위 테스트
 */
@UnitTest
@DisplayName("지하철 노선 생성에 대한 도메인 단위 테스트")
public class SubwayLineRegisterDomainTest {

    /**
     * given : 시작과 끝의 지하철 역이 두개 주어지고, 둘 사이의 거리와 이름이 주어지고<br>
     * when : 그 값으로 지하철 노선을 생성하면<br>
     * then : 지하철 노선이 생성된다.<br>
     */
    @Test
    @DisplayName("지하철 노선을 생성한다")
    void registerSubwayLine() {

        //given
        String name = "2호선";
        String color = "green";

        Station.Id id = new Station.Id(1L);
        String name1 = "강남역";
        Station upStation = Station.of(id, name1);

        Station.Id id2 = new Station.Id(2L);
        String name2 = "역삼역";
        Station downStation = Station.of(id2, name2);

        BigDecimal number = BigDecimal.TEN;
        Kilometer kilometer = Kilometer.of(number);

        //when
        SubwayLine subwayLine = SubwayLine.register(name, color, upStation, downStation, kilometer);

        //then
        Assertions.assertThat(subwayLine.getName()).isEqualTo(name);
        Assertions.assertThat(subwayLine.getColor()).isEqualTo(color);
        Assertions.assertThat(subwayLine.getSectionSize()).isEqualTo(1);
        Assertions.assertThat(subwayLine.existsUpStation(upStation.getId())).isTrue();

    }

}
