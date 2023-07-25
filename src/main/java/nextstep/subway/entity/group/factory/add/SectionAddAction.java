package nextstep.subway.entity.group.factory.add;

public interface SectionAddAction {

    default void action() {

        validate();
        addAction();
    }

    void validate();

    void addAction();

}
