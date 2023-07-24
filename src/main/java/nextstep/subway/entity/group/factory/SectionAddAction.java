package nextstep.subway.entity.group.factory;

public interface SectionAddAction {

    default void action() {

        validate();
        addAction();
    }

    void validate();

    void addAction();

}
