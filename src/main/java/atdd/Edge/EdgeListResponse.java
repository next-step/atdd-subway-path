package atdd.Edge;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class EdgeListResponse {
    private List<Edge> edges;

    @Builder
    public EdgeListResponse(List<Edge> edges){
        this.edges = edges;
    }

    public static EdgeListResponse of(List<Edge> edges){
        return EdgeListResponse.builder()
                .edges(edges)
                .build();
    }

}
