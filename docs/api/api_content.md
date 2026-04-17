# Content API 명세서

> Base URL: `http://localhost:8080/api/v1/contents`
> 모든 엔드포인트 인증 필요 (`Authorization: Bearer {token}`)

---

## 1. 게시글 목록 조회 (검색 포함)

| 항목 | 내용 |
|------|------|
| **메서드** | `GET` |
| **엔드포인트** | `/api/v1/contents` |

### Query Parameters

| 파라미터 | 타입 | 기본값 | 필수 | 설명 |
|----------|------|--------|------|------|
| page | int | 0 | 선택 | 페이지 번호 (0부터 시작) |
| size | int | 10 | 선택 | 페이지당 항목 수 |
| keyword | String | - | 선택 | 검색 키워드 (없으면 전체 조회) |
| searchType | String | TITLE | 선택 | 검색 기준: `TITLE` (제목) / `USERNAME` (작성자) |

### 요청 예시

```
# 전체 목록
GET /api/v1/contents?page=0&size=10

# 제목으로 검색
GET /api/v1/contents?keyword=스프링&searchType=TITLE

# 작성자 username으로 검색
GET /api/v1/contents?keyword=user1&searchType=USERNAME&page=0&size=5
```

### Response `200 OK`

```json
{
  "content": [
    {
      "id": 100,
      "title": "user3 테스트 게시글 25번",
      "body": "이것은 유저삼이 작성한 25번째 테스트 게시글입니다.",
      "createdBy": "user3",
      "createdAt": "2026-04-17T10:25:00"
    },
    {
      "id": 99,
      "title": "user3 테스트 게시글 24번",
      "body": "이것은 유저삼이 작성한 24번째 테스트 게시글입니다.",
      "createdBy": "user3",
      "createdAt": "2026-04-17T10:24:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10,
  "hasNext": true
}
```

---

## 2. 게시글 작성

| 항목 | 내용 |
|------|------|
| **메서드** | `POST` |
| **엔드포인트** | `/api/v1/contents` |

### Request Body

```json
{
  "title": "오늘의 모임",
  "body": "서울 강남역 근처에서 같이 밥 먹을 분 구합니다."
}
```

| 필드 | 타입 | 제약 |
|------|------|------|
| title | String | 최대 20자 |
| body | String | 필수 |

### Response `201 Created`

```json
{
  "id": 101,
  "title": "오늘의 모임",
  "body": "서울 강남역 근처에서 같이 밥 먹을 분 구합니다.",
  "createdBy": "user1",
  "createdAt": "2026-04-17T16:00:00",
  "updatedBy": "user1",
  "updatedAt": "2026-04-17T16:00:00"
}
```

---

## 3. 게시글 수정

| 항목 | 내용 |
|------|------|
| **메서드** | `PATCH` |
| **엔드포인트** | `/api/v1/contents/{contentId}` |
| **권한** | 작성자 본인만 가능 |

### Path Parameter

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| contentId | Long | 게시글 ID |

### Request Body

```json
{
  "title": "수정된 제목입니다",
  "body": "수정된 본문 내용입니다."
}
```

### Response `200 OK`

```json
{
  "id": 1,
  "title": "수정된 제목입니다",
  "body": "수정된 본문 내용입니다.",
  "author": "user1",
  "createdAt": "2026-04-17T10:01:00",
  "updatedAt": "2026-04-17T16:10:00"
}
```

### Error

| 상황 | 상태 코드 |
|------|-----------|
| 게시글 없음 | `404 Not Found` |
| 본인 게시글 아님 | `403 Forbidden` |

---

## 4. 게시글 삭제

| 항목 | 내용 |
|------|------|
| **메서드** | `DELETE` |
| **엔드포인트** | `/api/v1/contents/{contentId}` |
| **권한** | 작성자 본인만 가능 |

### Path Parameter

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| contentId | Long | 게시글 ID |

### Response `204 No Content`

- Body 없음

### Error

| 상황 | 상태 코드 |
|------|-----------|
| 게시글 없음 | `404 Not Found` |
| 본인 게시글 아님 | `403 Forbidden` |
