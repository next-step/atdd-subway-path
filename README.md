# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 🚀 1단계 - 구간 추가 요구사항 반영
### 기능 요구사항(완료 조건)
- 노선에 역 추가시 노선 가운데 추가 할 수 있다. (기존 구간의 역을 기준으로 새로운 구간을 추가)
- 노선에 역 추가시 노선 처음에 추가 할 수 있다. 
- 이미 등록되어있는 역은 노선에 등록될 수 없다. (기존 구간과 신규 구간이 모두 같을 순 없음)
- 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정

## 🚀 2단계 - 구간 제거 요구사항 반영
### 기능 요구사항(완료 조건)
- 노선에 등록된 역 제거 시 해당 역이 노선 가운데 있어도 제거할 수 있다.
- 노선에 등록된 역 제거 시 해당 역이 상행 종점역이어도 제거할 수 있다.
- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨 
- 중간역이 제거될 경우 재배치를 함 
  - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨. 거리는 두 구간의 거리의 합으로 정함

## 🚀 3단계 - 경로 조회 기능
### 요청/응답
- Request
  - source : 출발역 id
  - target : 도착역 id
~~~
HTTP/1.1 200
Request method:	GET
Request URI:	http://localhost:55494/paths?source=1&target=3
Headers: 	Accept=application/json
Content-Type=application/json; charset=UTF-8
~~~
- Response
  - stations: 출발역으로부터 도착역까지의 경로에 있는 역 목록
  - distance: 조회한 경로 구간의 거리
~~~
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
"name": "남부터미널역"
},
{
"id": 3,
"name": "양재역"
}
],
"distance": 5
}
~~~
