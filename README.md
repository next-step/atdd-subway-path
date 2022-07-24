# 지하철 노선도 미션

[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 🚀 1단계 - 단위 테스트 작성

### 성공하는 테스트 코드 먼저 작성하기

- [x] LineTest
- [x] LineServiceMockTest
- [x] LineServiceTest

### LineService 내 로직을 각 도메인 클래스에 옮기기

- [x] TDD 사이클로 리팩토링 작성해보기

## 🚀 2단계 - 지하철 구간 추가 리팩터링

### 인수 테스트 작성

- [x] 구간 추가 제약사항 변경
    - 모든 구간에 구간에 신규 역 등록이 가능하다.
    - 상행선 그리고 하행선이 구간에 모두 등록되어 있지 않아야 등록이 가능하다.
    - 상행선 또는 하행선에 역이 한가지는 등록되어 있어야 등록이 가능하다.
    - 기존 구간 사이에 신규 역이 추가되면 기존 구간의 길이보다 작아야 한다.
- [x] 노선 조회시 응답되는 역목록 정렬 순서 변경
    - 상행 종점 -> 하행 종점 순으로 반환한다.

## 🚀 3단계 - 지하철 구간 제거 리팩터링

- [x] 구간 삭제 제약사항 변경
    - 위치에 상관 없이 역 삭제가 가능하다.
    - A, B, C 중 B(중간역)이 삭제되는 경우 A - C 의 거리는 기존 A-B, B-C의 거리가 합쳐진다.
- [x] 기존 구간이 하나인 경우 제거할 수 없다.
- [x] 노선에 등록되어 있지 않은 역은 제거할 수 없다.

## 🚀 4단계 - 경로 조회 기능

- [x] path 인수 테스트 작성
    - 최단거리 path를 반환해야 한다.

- [x] path 기능 구현 및 단위 테스트 작성
    - 최단거리 path를 반환해야한다.
    - 출도착 역이 같은경우 예외가 발생한다.
    - path를 찾을 수 없으면 예외가 발생한다.
    - 역을 찾을 수 없으면 예외가 발생한다.

- [] pathService 단위 테스트 구현