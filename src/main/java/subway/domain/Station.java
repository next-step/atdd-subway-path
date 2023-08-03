package subway.domain;

import java.util.Objects;

public class Station {
    private final Id id;
    private final String name;

    public static Station register(String name) {
        return new Station(name);
    }

    public static Station of(Id id, String name) {
        return new Station(id, name);
    }


    private Station(String name) {
        this.id = new Id();
        this.name = name;
    }

    private Station(Id id, String name) {
        this.id = id;
        this.name = name;
    }

    public Id getId() {
        if (this.isNew()) {
            throw new IllegalArgumentException("아직 저장되지 않은 역입니다.");
        }
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isNew() {
        return id.isNew();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Id {
        private Long id;

        public Id() {
        }

        public Id(Long id) {
            this.id = id;
        }

        public Long getValue() {
            return id;
        }

        public boolean isNew() {
            return id == null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id idObject = (Id) o;
            return Objects.equals(id, idObject.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
