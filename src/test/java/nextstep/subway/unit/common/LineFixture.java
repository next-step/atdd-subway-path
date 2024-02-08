package nextstep.subway.unit.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.line.entity.Section;
import nextstep.subway.domain.station.entity.Station;
import nextstep.subway.interfaces.line.dto.LineRequest;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class LineFixture {
    public static class Entity {
        public static long id = 0;
        public static Line 라인생성(String name, String color, Section section) {
            Line line = Mockito.spy(new Line(name, color, section));
            when(line.getId()).thenReturn(id++);
            return line;
        }

        public static Line 라인생성(String name, String color, Section section, Section section2, Section section3) {
            Line line = Mockito.spy(new Line(name, color, section));
            line.add(section2);
            line.add(section3);
            when(line.getId()).thenReturn(id++);
            return line;
        }
        public static Section 구간생성(Station upStation, Station downStation, Long distance){
            Section section = Mockito.spy(Section.of(upStation, downStation, distance));
            when(section.getId()).thenReturn(id++);
            return section;
        }
    }

    public static class Request{
        public static LineRequest.Section 구간요청(Long upStationId, Long downStationId, Long distance) {
            Map<String, Long> body = new HashMap<>();
            body.put("upStationId", upStationId);
            body.put("downStationId", downStationId);
            body.put("distance", distance);
            ObjectMapper objectMapper = new ObjectMapper();
            return new ObjectMapper().convertValue(body, LineRequest.Section.class);
        }
    }
}
