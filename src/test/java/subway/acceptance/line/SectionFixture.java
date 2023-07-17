package subway.acceptance.line;

import subway.line.model.Line;
import subway.line.model.Section;
import subway.station.model.Station;

import java.util.HashMap;
import java.util.Map;

public class SectionFixture {
    public static Map<String, String> 구간_요청_만들기(final Long upStationId,
                                                final Long downStationId,
                                                final Long distance) {
        Map<String, String> request = new HashMap<>();
        request.put("downStationId", String.valueOf(downStationId));
        request.put("upStationId", String.valueOf(upStationId));
        request.put("distance", String.valueOf(distance));
        return request;
    }

    public static Line 이호선_기본구간_생성() {
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");

        Line line = Line.builder()
                .name("2호선")
                .color("bg-green-600")
                .build();

        final long distance = 10;

        Section section = Section.builder()
                .line(line)
                .upStation(강남역)
                .downStation(역삼역)
                .distance(distance)
                .build();
        line.addSection(section);
        return line;
    }

    public static Section 기본구간에_구간추가(Line line) {
        Station upStation = new Station(2L, "역삼역");
        Station downStation = new Station(5L, "잠실역");


        final long distance = 10;
        Section section = Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
        line.addSection(section);
        return section;
    }
}
