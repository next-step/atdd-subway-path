package nextstep.subway.utils;

import nextstep.subway.station.Station;

public class StationFixture {
    public static final Long BANGHWA_ID = 1L;
    public static final Long GANGDONG_ID = 2L;

    public static final Long MACHEON_ID = 3L;

    public static Station BanghwaStation() {
        return new Station("방화역");
    }

    public static Station GangdongStation() {
        return new Station("강동역");
    }

    public static Station MacheonStation() {
        return new Station("마천역");
    }
}
