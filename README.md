# 🚀 3단계 - 경로 조회 기능

# 요구사항

## 기능 요구사항

- `요구사항 설명`에서 제공되는 추가된 요구사항을 기반으로 **경로 조회 기능**을 구현하세요.
- 추가된 요구사항을 정의한 **인수 조건**을 도출하세요.
- 인수 조건을 검증하는 **인수 테스트**를 작성하세요.

## 프로그래밍 요구사항

- 인수 테스트 주도 개발 프로세스

  에 맞춰서 기능을 구현하세요.

  - `요구사항 설명`을 참고하여 인수 조건을 정의
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현

- 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.

  - 뼈대 코드의 인수 테스트를 참고

- ```
  인수 테스트 이후 기능 구현은 TDD로 진행하세요.
  ```

  - 도메인 레이어 테스트는 필수
  - 서비스 레이어 테스트는 선택

# 요구사항 설명

## 요청 / 응답 포맷

### Request

- source: 출발역 id
- target: 도착역 id

```http
HTTP/1.1 200 
Request method:	GET
Request URI:	http://localhost:55494/paths?source=1&target=3
Headers: 	Accept=application/json
		Content-Type=application/json; charset=UTF-8
```

### Response

- stations: 출발역으로부터 도착역까지의 경로에 있는 역 목록
- distance: 조회한 경로 구간의 거리

```http
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 09 May 2020 14:54:11 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
    "stations": [
        {
            "id": 1,
            "name": "교대역"
        },
        {
            "id": 4,
            "name": "남부터미널역"
        },
        {
            "id": 3,
            "name": "양재역"
        }
    ],
    "distance": 5
}
```

# 힌트

## 다익스트라 알고리즘 라이브러리 학습을 위한 학습 테스트

### JGraphTest

- 경로 탐색을 위한 라이브러리 JGraph의 학습 테스트

## 최단 경로 라이브러리

- jgrapht 라이브러리를 활용하면 간편하게 최단거리를 조회할 수 있음
- 정점(vertext)과 간선(edge), 그리고 가중치 개념을 이용
  - 정점: 지하철역(Station)
  - 간선: 지하철역 연결정보(Section)
  - 가중치: 거리
- 최단 거리 기준 조회 시 가중치를 `거리`로 설정

```java
@Test
public void getDijkstraShortestPath() {
    WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);
    graph.addVertex("v1");
    graph.addVertex("v2");
    graph.addVertex("v3");
    graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
    graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
    graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

    DijkstraShortestPath dijkstraShortestPath
            = new DijkstraShortestPath(graph);
    List<String> shortestPath 
            = dijkstraShortestPath.getPath("v3", "v1").getVertexList();

    assertThat(shortestPath.size()).isEqualTo(3);
}
```

> [jgrapht graph-algorithms](https://jgrapht.org/guide/UserOverview#graph-algorithms)

## 외부 라이브러리 테스트

- 외부 라이브러리의 구현을 수정할 수 없기 때문에 **단위 테스트를 하지 않음**
- 외부 라이브러리를 사용하는 직접 구현하는 로직을 검증해야 함
- 직접 구현하는 로직 검증 시 외부 라이브러리 부분은 실제 객체를 활용

## 인수 테스트 픽스쳐 예시

```java
@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }
    ...
```

## 예외 상황 예시

- 출발역과 도착역이 같은 경우
- 출발역과 도착역이 연결이 되어 있지 않은 경우
- 존재하지 않은 출발역이나 도착역을 조회 할 경우

## 미션 수행 순서

### 인수 테스트 이후 TDD 예시



TDD의 방향보다 테스트를 통해 구현할 기능을 명세하는것과 리팩터링이 더 중요합니다!

#### Outside In TDD

- 컨트롤러 구현 이후 서비스 구현 시 바로 기능 구현에 앞서 단위 테스트 먼저 작성
- 서비스 레이어의 단위 테스트 목적은 비즈니스 플로우를 검증하는 것이며 이 때 협력 객체는 stubbing을 활용하여 대체함
- 단위 테스트 작성 후 해당 단위 테스트를 만족하는 기능을 구현한 다음 리팩터링 진행
- 그 다음 기능 구현은 방금 전 사이클에서 stubbing 한 객체를 대상으로 진행하면 수월하게 TDD 사이클을 진행할 수 있음
- 외부 라이브러리를 활용한 로직을 검증할 때는 가급적 실제 객체를 활용
- Happy 케이스에 대한 부분만 구현( Side 케이스에 대한 구현은 다음 단계에서 진행)

#### Inside Out TDD

- 인수 테스트를 작성하며 도메인에 대한 이해도를 높이고 기능 구현 전 도메인 설계를 진행
- 도메인 설계 후 도메인 객체에 대한 단위 테스트 작성을 시작으로 TDD 사이클 시작
- 해당 도메인의 단위 테스트를 통해 도메인의 역할과 경계를 설계
- 도메인의 구현이 끝나면 해당 도메인과 관계를 맺는 객체에 대해 기능 구현 시작

```plaintext
ex) 경로 조회를 수행하는 도메인 구현 예시
  - 1. PathFinder 라는 클래스 작성 후 경로 조회를 위한 테스트를 작성
  - 2. 경로 조회 메서드에서 Line을 인자로 받고 그 결과로 원하는 응답을 리턴하도록 테스트 완성
  - 3. 테스트를 성공시키기 위해 JGraph의 실제 객체를 활용(테스트에서는 알 필요가 없음)
```



두 방향성을 모두 사용해보시고 테스트가 협력 객체의 세부 구현에 의존하는 경우(가짜 협력 객체 사용)와 테스트 대상이 협력 객체와 독립적이지 못하고 변경에 영향을 받는 경우(실제 협력 객체 사용)를 모두 경험해보세요 :)

# TODO

#### 인수테스트

- [x] GIVEN 지하철 노선들을 생성하고 구간을 추가 후</br>
  WHEN 출발역과 도착역을 입력하면</br>
  THEN 구간과 거리를 알 수 있다

- [ ] GIVEN 지하철 노선들을 생성하고 구간을 추가 후</br>
  WHEN 출발역과 도착역을 같게 입력하면</br>
  THEN 에러 처리와 함께 '출발역과 도착역은 같을 수 없습니다.' 라는 메세지가 출력된다

- [ ] GIVEN 지하철 노선들을 생성하고 구간을 추가 후</br>
  WHEN 출발역과 도착역이 연결이 되지 않게 입력하면</br>
  THEN 에러 처리와 함께 '출발역과 도착역은 연결되어 있어야 합니다.' 라는 메세지가 출력된다

- [ ] GIVEN 지하철 노선들을 생성하고 구간을 추가 후</br>
  WHEN 존재 하지 않는 역을 입력하면</br>
  THEN 에러 처리와 함께 '입력한 역을 찾을 수 없습니다.' 라는 메세지가 출력된다

#### 단위테스트

- [x] case 1: 시작역과 도착역을 포함하는 라인을 찾을 수 있다
    1. 시작역을 기준으로 일치하는 라인을 먼저 찾음
    2. 그 후 도착역을 기준으로 찾음

- [ ] case 2: 시작역과 도착역이 같을 경우 에러 발생

- [ ] case 3: 시작역과 도착역을 포함하는 라인을 찾지 못했을 경우 에러 발생

- [ ] case 4: 시작역과 도착역을 찾을 수 없는 경우 에러 발생

- [ ] case 5: 찾은 라인을 기준으로 최소 경로를 찾을 수 있다
