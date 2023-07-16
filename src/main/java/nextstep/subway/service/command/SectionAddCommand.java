package nextstep.subway.service.command;

public interface SectionAddCommand {

    Long getUpStationId();

    Long getDownStationId();

    Long getDistance();
}
