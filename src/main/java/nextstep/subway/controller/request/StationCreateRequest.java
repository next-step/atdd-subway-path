package nextstep.subway.controller.request;

import nextstep.subway.service.command.StationCreateCommand;

public class StationCreateRequest implements StationCreateCommand {
    private String name;

    public String getName() {
        return name;
    }
}
