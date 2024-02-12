package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationsTest {

    private Station 강남역;
    private Station 선릉역;
    private Station 양재역;
    private Stations 지하철역;

    @BeforeEach
    void setUp() {
        강남역 = GANGNAM_STATION.toStation(1L);
        선릉역 = SEOLLEUNG_STATION.toStation(2L);
        양재역 = YANGJAE_STATION.toStation(3L);
        지하철역 = new Stations(List.of(강남역, 선릉역, 양재역));
    }

    @Test
    void 실패_지하철역_정보가_없을경우_예외가_발생한다() {
        assertThatThrownBy(() -> new Stations(new ArrayList<>()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("지하철역이 존재하지 않습니다.");
    }

    /**
     * 지하철역: 강남역, 선릉역, 양재역
     */
    @Test
    void 실패_지하철역_조회시_존재하지_않을경우_예외가_발생한다() {
        assertThatThrownBy(() -> 지하철역.findStationBy(4L))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }

}
