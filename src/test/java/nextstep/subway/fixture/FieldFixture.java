package nextstep.subway.fixture;


public enum FieldFixture {
    식별자_아이디("id"),
    역_아이디("stationId"),
    역_이름("name"),

    노선_이름("name"),
    노선_색깔("color"),
    노선_상행_종점역_ID("upStationId"),
    노선_하행_종점역_ID("downStationId"),
    노선_간_거리("distance"),
    노선_내_역_목록("stations"),
    노선_내_역_아이디("stations.id"),
    노선_내_역_이름("stations.name"),

    경로_내_역_목록("stations"),
    경로_내_역_아이디_목록("stations.id"),
    경로_조회_출발지_아이디("source"),
    경로_조회_도착지_아이디("target"),
    ;

    private final String value;

    FieldFixture(String value) {
        this.value = value;
    }

    public String 필드명() {
        return value;
    }
}
