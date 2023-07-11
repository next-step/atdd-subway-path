package nextstep.subway.service.command;

public interface LineCreateCommand {

    String getName();

    String getColor();

    Long getUpStationId();

    Long getDownStationId();

    Long getDistance();
}
