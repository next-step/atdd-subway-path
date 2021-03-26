package nextstep.subway.path.ui;

import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.common.NotFoundException;
import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.domain.InvalidPathException;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {
    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findPath(@AuthenticationPrincipal LoginMember loginMember, @RequestParam Long source, @RequestParam Long target, @RequestParam PathType type) {
        return ResponseEntity.ok(pathService.findPath(loginMember, source, target, type));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity handleIllegalArgumentException() {
        return ResponseEntity.badRequest().body("출발역 혹은 도착역을 찾을 수 없습니다.");
    }

    @ExceptionHandler({NotFoundException.class, InvalidPathException.class})
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
