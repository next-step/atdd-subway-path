package nextstep.subway.testhelper;

import java.util.HashMap;
import java.util.Map;

public class SectionFixture {

    private Map<String, String> 삼성역_부터_선릉역_구간_params;
    private Map<String, String> 선릉역_부터_교대역_구간_params;
    private Map<String, String> 삼성역_부터_강남역_구간_params;

    public SectionFixture(StationFixture stationFixture) {
        삼성역_부터_선릉역_구간_params = new HashMap<>();
        삼성역_부터_선릉역_구간_params.put("upStationId", stationFixture.get삼성역_ID().toString());
        삼성역_부터_선릉역_구간_params.put("downStationId", stationFixture.get선릉역_ID().toString());
        삼성역_부터_선릉역_구간_params.put("distance", "10");

        선릉역_부터_교대역_구간_params = new HashMap<>();
        선릉역_부터_교대역_구간_params.put("upStationId", stationFixture.get선릉역_ID().toString());
        선릉역_부터_교대역_구간_params.put("downStationId", stationFixture.get교대역_ID().toString());
        선릉역_부터_교대역_구간_params.put("distance", "10");

        삼성역_부터_강남역_구간_params = new HashMap<>();
        삼성역_부터_강남역_구간_params.put("upStationId", stationFixture.get삼성역_ID().toString());
        삼성역_부터_강남역_구간_params.put("downStationId", stationFixture.get강남역_ID().toString());
        삼성역_부터_강남역_구간_params.put("distance", "10");
    }

    public Map<String, String> get삼성역_부터_선릉역_구간_params() {
        return 삼성역_부터_선릉역_구간_params;
    }

    public Map<String, String> get선릉역_부터_교대역_구간_params() {
        return 선릉역_부터_교대역_구간_params;
    }

    public Map<String, String> get삼성역_부터_강남역_구간_params() {
        return 삼성역_부터_강남역_구간_params;
    }
}
