package nextstep.subway.applicaion.dto;

import com.sun.istack.NotNull;

public class StationRequest {
    @NotNull
    private String name;

    public String getName() {
        return name;
    }
}
