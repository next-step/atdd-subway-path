package subway.application.in;

import subway.domain.SubwayLine;

public interface SubwayLineUpdateUsecase {

    void updateSubwayLine(Command command);
    class Command {

        private SubwayLine.Id id;
        private UpdateContents updateContents;

        public Command(SubwayLine.Id id, UpdateContents updateContents) {
            this.id = id;
            this.updateContents = updateContents;
        }

        public SubwayLine.Id getId() {
            return id;
        }

        public String getName() {
            return updateContents.getName();
        }

        public String getColor() {
            return updateContents.getColor();
        }

        public static class UpdateContents {
            private String name;
            private String color;

            public UpdateContents(String name, String color) {
                this.name = name;
                this.color = color;
            }


            public String getName() {
                return name;
            }

            public String getColor() {
                return color;
            }
        }

    }
}
