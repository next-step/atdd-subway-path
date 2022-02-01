<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-6.14.15-blue">
  <img alt="node" src="https://img.shields.io/badge/node-14.18.2-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage

#### webpack server 구동

```
npm run dev
```

#### application 구동

```
./gradlew bootRun
```

## 🚀 1단계 - 구간 추가 기능 변경

- 역 사이에 새로운 역을 등록할 경우
- 새로운 역을 상행 종점으로 등록할 경우
- 새로운 역을 하행 종점으로 등록할 경우
- 노선 조회시 응답되는 역 목록 수정

### 예외 케이스

- 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
 
## 🚀 2단계 - 구간 제거 기능 변경

- 역 위치에 상관 없이 구간 삭제가 가능하도록 수정
- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- 중간역이 제거될 경우 재배치를 함
  - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
  - 거리는 두 구간의 거리의 합으로 정함

### 예외 케이스

- 구간이 하나인 노선에서 마지막 구간을 제거할 수 없음
- 노선에 등록되어있지 않은 역을 제거할 수 없음
 

## 🚀 3단계 - 경로 조회
- 임의의 역과 역 사이의 최단 경로를 조회할 수 있다.

### 예외 케이스
- 출발역과 도착역이 같은 경우
- 출발역과 도착역이 연결이 되어 있지 않은 경우
- 존재하지 않은 출발역이나 도착역을 조회 할 경우

