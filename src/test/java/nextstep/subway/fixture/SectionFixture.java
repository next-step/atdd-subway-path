package nextstep.subway.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.FieldFixture.노선_간_거리;
import static nextstep.subway.fixture.FieldFixture.노선_상행_종점역_ID;
import static nextstep.subway.fixture.FieldFixture.노선_하행_종점역_ID;
import static nextstep.subway.fixture.StationFixture.강남역;
import static nextstep.subway.fixture.StationFixture.선릉역;
import static nextstep.subway.fixture.StationFixture.역삼역;

public enum SectionFixture {

    강남_역삼_구간(4, 강남역, 역삼역),
    역삼_선릉_구간(6, 역삼역, 선릉역),
    ;

    private final int distance;
    private final StationFixture upStation;
    private final StationFixture downStation;

    SectionFixture(int distance, StationFixture upStation, StationFixture downStation) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public int 노선_간_거리() {
        return distance;
    }

    public StationFixture 상행역() {
        return upStation;
    }

    public StationFixture 하행역() {
        return downStation;
    }

    public Map<String, String> 요청_데이터_생성(String 상행역_id, String 하행역_id) {
        Map<String, String> params = new HashMap<>();
        params.put(노선_상행_종점역_ID.필드명(), 상행역_id);
        params.put(노선_하행_종점역_ID.필드명(), 하행역_id);
        params.put(노선_간_거리.필드명(), String.valueOf(노선_간_거리()));
        return params;
    }

    public Section 엔티티_생성(Line 호선) {
        return new Section(호선, 상행역().엔티티_생성(), 하행역().엔티티_생성(), 노선_간_거리());
    }
}
