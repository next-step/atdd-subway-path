package atdd.line;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

public class LineDto {

    @Getter
    public static class Request {
        private String name;
        private LocalTime startTime;
        private LocalTime endTime;
        private int intervalTime;
        private int extraFare;

        public Line toEntity() {
            return Line.builder()
                    .name(name)
                    .startTime(startTime)
                    .endTime(endTime)
                    .intervalTime(intervalTime)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Response {

        private String name;
        private LocalTime startTime;
        private LocalTime endTime;
        private int intervalTime;

        static LineDto.Response from(Line line) {
            return Response.builder()
                    .name(line.getName())
                    .startTime(line.getStartTime())
                    .endTime(line.getEndTime())
                    .intervalTime(line.getIntervalTime())
                    .build();
        }
    }
}
