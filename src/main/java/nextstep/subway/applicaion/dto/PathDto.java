package nextstep.subway.applicaion.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Station;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathDto {
    private List<Station> shortestPath;
    private int shortestDistance;

    public static PathDto of(List<Station> shortestPath, int shortestDistance) {
        return new PathDto(shortestPath, shortestDistance);
    }

}
