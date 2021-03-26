package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.domain.SubwayGraph;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphService {
    private LineService lineService;

    public GraphService(LineService lineService) {
        this.lineService = lineService;
    }

    public SubwayGraph findGraph(LoginMember loginMember, PathType type) {
        List<Line> lines = lineService.findLines(loginMember);
        return new SubwayGraph(lines, type);
    }
}
