package nextstep.subway.fixture;


import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.FieldFixture.식별자_아이디;
import static nextstep.subway.fixture.FieldFixture.역_이름;

public enum StationFixture {
    정자역,
    양재역,
    강남역,
    신사역,
    역삼역,
    선릉역,
    삼성역,
    서울대입구역,
    범계역,
    교대역,
    남부터미널역,
    ;

    public String 역_이름() {
        return name();
    }

    public Map<String, String> 요청_데이터_생성() {
        Map<String, String> params = new HashMap<>();
        params.put(역_이름.필드명(), 역_이름());
        return params;
    }

    public Station 엔티티_생성() {
        return new Station(역_이름());
    }

    public Station 엔티티_생성(Long id) {
        Station station = new Station(역_이름());
        ReflectionTestUtils.setField(station, 식별자_아이디.필드명(), id);
        return station;
    }
}
