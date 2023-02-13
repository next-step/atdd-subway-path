package nextstep.subway.fixture;

import nextstep.subway.domain.Line;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.FieldFixture.노선_간_거리;
import static nextstep.subway.fixture.FieldFixture.노선_상행_종점역_ID;
import static nextstep.subway.fixture.FieldFixture.노선_색깔;
import static nextstep.subway.fixture.FieldFixture.노선_이름;
import static nextstep.subway.fixture.FieldFixture.노선_하행_종점역_ID;

public enum LineFixture {
    이호선("2호선", "green"),
    사호선("4호선", "blue"),
    신분당선("신분당선", "red"),
    ;

    private final String name;
    private final String color;

    LineFixture(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String 노선_이름() {
        return name;
    }

    public String 노선_색깔() {
        return color;
    }


    public Map<String, String> 생성_요청_데이터_생성(Long 상행종점역_id, Long 하행종점역_id, int 노선_거리) {
        Map<String, String> params = new HashMap<>();
        params.put(노선_이름.필드명(), 노선_이름());
        params.put(노선_색깔.필드명(), 노선_색깔());
        params.put(노선_간_거리.필드명(), String.valueOf(노선_거리));
        params.put(노선_상행_종점역_ID.필드명(), String.valueOf(상행종점역_id));
        params.put(노선_하행_종점역_ID.필드명(), String.valueOf(하행종점역_id));
        return params;
    }

    public Map<String, String> 수정_요청_데이터_생성() {
        Map<String, String> params = new HashMap<>();
        params.put(노선_이름.필드명(), 노선_이름());
        params.put(노선_색깔.필드명(), 노선_색깔());
        return params;
    }

    public Line 엔티티_생성() {
        return new Line(노선_이름(), 노선_색깔());
    }
}
