package nextstep.subway.domain.command;

public interface SectionAddCommand {

    Long getUpStationId();

    Long getDownStationId();

    Long getDistance();
}
