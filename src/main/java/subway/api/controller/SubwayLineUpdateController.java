package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.application.in.SubwayLineUpdateUsecase;
import subway.domain.SubwayLine;

@RestController
public class SubwayLineUpdateController {

    private final SubwayLineUpdateUsecase subwayLineUpdateUsecase;

    public SubwayLineUpdateController(SubwayLineUpdateUsecase subwayLineUpdateUsecase) {
        this.subwayLineUpdateUsecase = subwayLineUpdateUsecase;
    }

    @PutMapping("/subway-lines/{id}")
    public ResponseEntity<Void> updateSubwayLine(@PathVariable Long id, @RequestBody Request request) {
        SubwayLineUpdateUsecase.Command command = mapFrom(id, request);
        subwayLineUpdateUsecase.updateSubwayLine(command);
        return ResponseEntity.ok().build();
    }

    private SubwayLineUpdateUsecase.Command mapFrom(Long id, Request request) {
        SubwayLine.Id domainId = new SubwayLine.Id(id);
        SubwayLineUpdateUsecase.Command.UpdateContents contents = new SubwayLineUpdateUsecase.Command.UpdateContents(request.getName(), request.getColor());
        return new SubwayLineUpdateUsecase.Command(domainId, contents);
    }

    public static class Request {
        private String name;
        private String color;

        public Request(String name, String color) {
            this.name = name;
            this.color = color;
        }

        private Request() {
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }
    }

}
