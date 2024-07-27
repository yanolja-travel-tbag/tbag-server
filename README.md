## TBAG

<img width="1039" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/8a8fb197-b981-4c3d-8372-53d9050624a5">


### 2024년 [트래블 이노베이션 아이디어 공모전] 대상 수상작 👑 
야놀자리서치, 야놀자, 인터파크트리플 공동주최


### 소개

TBAG은 K-드라마, 영화, 아이돌 등의 팬들을 위한 여행 가이드 어플리케이션입니다. 
한국을 방문하는 외국인 관광객들이 본인이 좋아하는 아이돌, 배우 또는 드라마 등 K-콘텐츠와 관련된 명소를 쉽게 찾고 즐길 수 있도록 다양한 정보를 제공합니다.
또한, 직접 여행 일정을 만들어 최단 시간으로 즐기는 최적 경로를 찾을 수 있습니다.

### 주요 기능

1. **K-콘텐츠 여행지 지도**
   - 드라마, 영화, 아이돌 촬영지 정보를 한눈에 볼 수 있는 종합 지도 제공
   - 사용자가 원하는 콘텐츠의 장소 위치를 확인하고 추가 정보 제공

2. **여행 경로 최적화**
   - 사용자가 계획한 여행 일정에 따라 최적의 경로를 추천
   - 대중교통을 통한 최적 경로와 예상 시간을 제공하여 효율적인 여행 계획 지원

3. **언어 번역 서비스**
   - 장소 정보와 콘텐츠 설명을 다양한 언어로 제공하여 언어 장벽 최소화

4. **콘텐츠 필터 및 검색**
   - 아이돌, 드라마, 영화 등 다양한 필터를 통해 원하는 콘텐츠 검색 가능
   - 검색 결과와 관련된 장소 목록 제공

5. **여행 일정 관리**
   - 여행 일정을 쉽게 생성하고 관리할 수 있는 기능 제공
   - 일정에 따라 추천 장소와 경로를 자동으로 생성

| 시작하기        |
|---------------|
|<img width="1041" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/d062db94-e8db-44d9-9475-4a8701ac8d34"> |

| 메인 / 서비스 메뉴        |
|---------------|
|<img width="1040" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/30f34207-bff4-4faa-b7c9-4aff36859ca3"> |

| 지도        |
|---------------|
|<img width="1042" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/418b2c84-951c-4947-9e15-a318ac1f97b0"> |

| 검색        |
|---------------|
|<img width="1040" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/b2c9c7cc-4306-41a9-a34b-b07da69cce3e">|

| 콘텐츠       |
|---------------|
|<img width="1042" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/10ebb182-7dc6-436d-a4b1-38a78810cd29">|

| 히스토리       |
|---------------|
|<img width="1043" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/0167f13f-2395-43ba-9453-316113fee7f6">|

| 여행일정 / 최적경로        |
|---------------|
|<img width="1041" alt="image" src="https://github.com/yanolja-travel-tbag/tbag-server/assets/74501631/7544e156-59f1-490d-a0ee-c174971a2abd">|


### 프로젝트 기간
2024.05.01-2024.07.08
- 05.01-06.03 기획 및 디자인
- 06.04-07.08 개발

### 기술 스택

- **Frontend**
  - 개발 프레임워크: React 18
  - 라우팅: react-router-dom 6
  - 스타일링: TailwindCSS
  - 상태 관리: Zustand, React Query v5
  - 지도: Naver Maps API
  - 기타: Vite, axios, Storybook 8

- **Backend**
  - 서버 프레임워크: Spring Boot 2.7.10, Spring Data JPA
  - 데이터베이스: MySQL
  - 언어: Java 11
  - ETC: AWS, Redis, Kakao/Google OAuth
  - API: Google Places API, Google Map Distance Matrix API, TMDB API, Spotify API, DeepL Translation API

### 사용 데이터 및 API

- **촬영지 및 방문지 데이터**
  - 한국관광공사 미디어콘텐츠 영상 촬영지 데이터
  - Google Places API

- **드라마/영화 데이터**
  - TMDB API를 통한 드라마, 영화, 배우, 캐릭터 정보 수집

- **아티스트 데이터**
  - Spotify API를 통한 아티스트 정보
  - 네이버 인물 정보 크롤링

- **번역 데이터**
  - DeepL Translation API와 Google Translation Library를 이용한 다국어 처리
   
- **경로 데이터**
  - Google Map Distance Matrix API를 통한 최적 경로 정보

  
### Preview
| 소셜 로그인 및 회원가입 |                                                   메인 바텀시트                                                   | 장소 상세 바텀시트 |
|:---:|:-----------------------------------------------------------------------------------------------------------:|:---:|
| ![google-social-login](https://github.com/user-attachments/assets/768eaf66-f29a-4a2d-b28d-296a74734ffb) | ![main-bottomsheet-filter](https://github.com/user-attachments/assets/4adfc6a2-69d3-4868-8be7-8de442e6299f) | ![place-detail-bottomsheet](https://github.com/user-attachments/assets/5a709ea5-0ddc-4291-9167-aab60a6e9911) |
| 지도 및 마커 필터 |                                                   연예인 검색                                                    | 장소 검색 |
| ![map-marker-filter](https://github.com/user-attachments/assets/2f101e8d-2dee-4e6f-a70a-de2116ecc14b) |      ![search-artist](https://github.com/user-attachments/assets/a085c01f-e18a-456a-ab3b-2d3f0ea526bb)      | ![search-place](https://github.com/user-attachments/assets/ac4f4649-e5c8-40a8-ba82-3688c4d1284f) |
| 콘텐츠 및 상세 정보 |                                                    히스토리                                                     | 여행 일정 |
| ![content-contentdetail](https://github.com/user-attachments/assets/b18fef1d-7be8-4681-ae43-aa1aeec7360a) |         ![history](https://github.com/user-attachments/assets/15691115-8296-44a1-ae55-f76ba760e193)         | ![add-schedule](https://github.com/user-attachments/assets/2f3be3f1-7c07-4050-9a60-b37ba3c6e89d) |
| 여행 경로 최적화 |                                                   콘텐츠 필터                                                    |
| ![optimize-path](https://github.com/user-attachments/assets/114df763-f191-4143-87f2-4a829ef143b2) |     ![content-filter](https://github.com/user-attachments/assets/ebfd9810-de4e-432b-84c7-3d1271074279)      |
| 언어 선택 및 국제화 |                                               다른 국제화 적용 모습                                                  |
| ![language-i18n](https://github.com/user-attachments/assets/70662141-1650-4a71-a019-9f6708749f20) | ![language-i18n2](https://github.com/user-attachments/assets/256d4fdb-e2bf-4559-a496-c2b10cc561f3) |

