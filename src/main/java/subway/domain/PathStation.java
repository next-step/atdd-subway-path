package subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@EqualsAndHashCode(of = {"id", "name"})
public class PathStation {

    private final PathStation.Id id;
    private final String name;

    public static PathStation of(PathStation.Id id, String name) {
        return new PathStation(id, name);
    }

    private PathStation(PathStation.Id id, String name) {
        this.id = id;
        this.name = name;
    }

    public void validate() {
        if (id.isNew()) {
            throw new IllegalArgumentException("아직 저장되지 않은 역입니다.");
        }
    }

    @EqualsAndHashCode(of = {"id"})
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Id {
        private Long id;

        private Id(Long id) {
            this.id = id;
        }
        public static Id of(Long id) {
            return new Id(id);
        }
        public static Id register() {
            return new Id();
        }

        public Long getValue() {
            return id;
        }

        public boolean isNew() {
            return id == null;
        }

    }
}
