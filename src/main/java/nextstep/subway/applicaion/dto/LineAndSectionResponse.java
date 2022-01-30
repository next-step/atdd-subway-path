package nextstep.subway.applicaion.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LineAndSectionResponse {

    @JsonUnwrapped
    private LineResponse lineResponse;
    private List<StationResponse> stations = new ArrayList();

    private LineAndSectionResponse() {
    }

    public static LineAndSectionResponse of(Long id,
                                            String name,
                                            String color,
                                            LocalDateTime createdDate,
                                            LocalDateTime modifiedDate,
                                            List<Station> allStations) {

        LineAndSectionResponse result = new LineAndSectionResponse();
        result.lineResponse = new LineResponse(id, name, color, createdDate, modifiedDate);
        result.stations = allStations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName(), it.getCreatedDate(), it.getModifiedDate()))
                .collect(toList());

        return result;
    }

    @JsonIgnore
    public long getLineId() {
        return lineResponse.getId();
    }

    @JsonIgnore
    public String getLineName() {
        return lineResponse.getName();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

}
