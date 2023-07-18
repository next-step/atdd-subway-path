package nextstep.subway.controller.request;

import nextstep.subway.domain.command.StationCreateCommand;

public class StationCreateRequest implements StationCreateCommand {
    private String name;

    public String getName() {
        return name;
    }
}
