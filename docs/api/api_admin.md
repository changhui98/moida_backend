# Admin API 명세서

> Base URL: `http://localhost:8080/api/v1/admin`
> 모든 엔드포인트 `ADMIN` 권한 필요 (`Authorization: Bearer {admin_token}`)

---

# 유저 관리 (`/api/v1/admin/users`)

## 1. 유저 목록 조회

| 항목 | 내용 |
|------|------|
| **메서드** | `GET` |
| **엔드포인트** | `/api/v1/admin/users` |

### Query Parameters

| 파라미터 | 타입 | 기본값 | 설명 |
|----------|------|--------|------|
| page | int | 0 | 페이지 번호 |
| size | int | 10 | 페이지당 항목 수 |

### 요청 예시

```
GET /api/v1/admin/users?page=0&size=10
```

### Response `200 OK`

```json
{
  "content": [
    {
      "id": "aaaaaaaa-0000-0000-0000-000000000001",
      "username": "admin",
      "nickname": "관리자",
      "userEmail": "admin@sagwim.com",
      "address": "서울 강남구 삼성동",
      "isDeleted": false
    },
    {
      "id": "aaaaaaaa-0000-0000-0000-000000000002",
      "username": "user1",
      "nickname": "유저일",
      "userEmail": "user1@sagwim.com",
      "address": "서울 마포구 합정동",
      "isDeleted": false
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 4,
  "totalPages": 1,
  "hasNext": false
}
```

---

## 2. 유저 단건 조회

| 항목 | 내용 |
|------|------|
| **메서드** | `GET` |
| **엔드포인트** | `/api/v1/admin/users/{username}` |

### Path Parameter

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| username | String | 조회할 유저의 username |

### 요청 예시

```
GET /api/v1/admin/users/user1
```

### Response `200 OK`

```json
{
  "id": "aaaaaaaa-0000-0000-0000-000000000002",
  "username": "user1",
  "nickname": "유저일",
  "userEmail": "user1@sagwim.com",
  "role": "USER",
  "address": "서울 마포구 합정동",
  "createAt": "2026-04-17T10:00:00",
  "modifiedAt": "2026-04-17T10:00:00",
  "isDeleted": false,
  "deletedAt": null
}
```

### Error

| 상황 | 상태 코드 |
|------|-----------|
| 유저 없음 | `404 Not Found` |

---

## 3. 유저 삭제 (소프트 딜리트)

| 항목 | 내용 |
|------|------|
| **메서드** | `DELETE` |
| **엔드포인트** | `/api/v1/admin/users/{username}` |

### Path Parameter

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| username | String | 삭제할 유저의 username |

### Response `204 No Content`

- Body 없음

---

# 게시글 관리 (`/api/v1/admin/contents`)

## 4. 게시글 목록 조회 (검색 포함)

| 항목 | 내용 |
|------|------|
| **메서드** | `GET` |
| **엔드포인트** | `/api/v1/admin/contents` |
| **특이사항** | 소프트 딜리트된 게시글 포함 전체 조회 |

### Query Parameters

| 파라미터 | 타입 | 기본값 | 필수 | 설명 |
|----------|------|--------|------|------|
| page | int | 0 | 선택 | 페이지 번호 |
| size | int | 10 | 선택 | 페이지당 항목 수 |
| keyword | String | - | 선택 | 검색 키워드 (없으면 전체 조회) |
| searchType | String | TITLE | 선택 | `TITLE` (제목) / `USERNAME` (작성자) |

### 요청 예시

```
# 전체 목록 (삭제된 게시글 포함)
GET /api/v1/admin/contents?page=0&size=10

# 제목으로 검색
GET /api/v1/admin/contents?keyword=테스트&searchType=TITLE

# 특정 유저 게시글 검색
GET /api/v1/admin/contents?keyword=admin&searchType=USERNAME&page=0&size=25
```

### Response `200 OK`

```json
{
  "content": [
    {
      "id": 25,
      "title": "관리자 테스트 게시글 25번",
      "body": "이것은 관리자가 작성한 25번째 테스트 게시글입니다.",
      "userId": "aaaaaaaa-0000-0000-0000-000000000001",
      "createdBy": "admin",
      "createdDate": "2026-04-17T09:25:00",
      "lastModifiedBy": "admin",
      "lastModifiedDate": "2026-04-17T09:25:00",
      "deletedBy": null,
      "deletedDate": null
    },
    {
      "id": 10,
      "title": "관리자 테스트 게시글 10번",
      "body": "이것은 관리자가 작성한 10번째 테스트 게시글입니다.",
      "userId": "aaaaaaaa-0000-0000-0000-000000000001",
      "createdBy": "admin",
      "createdDate": "2026-04-17T09:10:00",
      "lastModifiedBy": "admin",
      "lastModifiedDate": "2026-04-17T09:10:00",
      "deletedBy": "admin",
      "deletedDate": "2026-04-17T14:00:00"
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

## 5. 게시글 단건 조회

| 항목 | 내용 |
|------|------|
| **메서드** | `GET` |
| **엔드포인트** | `/api/v1/admin/contents/{contentId}` |

### Path Parameter

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| contentId | Long | 게시글 ID |

### 요청 예시

```
GET /api/v1/admin/contents/1
```

### Response `200 OK`

```json
{
  "id": 1,
  "title": "관리자 테스트 게시글 1번",
  "body": "이것은 관리자가 작성한 1번째 테스트 게시글입니다.",
  "userId": "aaaaaaaa-0000-0000-0000-000000000001",
  "createdBy": "admin",
  "createdDate": "2026-04-17T09:01:00",
  "lastModifiedBy": "admin",
  "lastModifiedDate": "2026-04-17T09:01:00",
  "deletedBy": null,
  "deletedDate": null
}
```

---

## 6. 게시글 수정

| 항목 | 내용 |
|------|------|
| **메서드** | `PATCH` |
| **엔드포인트** | `/api/v1/admin/contents/{contentId}` |
| **특이사항** | 삭제된 게시글은 수정 불가 |

### Path Parameter

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| contentId | Long | 게시글 ID |

### Request Body

```json
{
  "title": "관리자 수정 제목",
  "body": "관리자가 수정한 본문 내용입니다."
}
```

### Response `200 OK`

```json
{
  "id": 1,
  "title": "관리자 수정 제목",
  "body": "관리자가 수정한 본문 내용입니다.",
  "userId": "aaaaaaaa-0000-0000-0000-000000000001",
  "createdBy": "admin",
  "createdDate": "2026-04-17T09:01:00",
  "lastModifiedBy": "admin",
  "lastModifiedDate": "2026-04-17T16:30:00",
  "deletedBy": null,
  "deletedDate": null
}
```

### Error

| 상황 | 상태 코드 |
|------|-----------|
| 게시글 없음 | `404 Not Found` |
| 이미 삭제된 게시글 | `409 Conflict` |

---

## 7. 게시글 삭제 (소프트 딜리트)

| 항목 | 내용 |
|------|------|
| **메서드** | `DELETE` |
| **엔드포인트** | `/api/v1/admin/contents/{contentId}` |

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
| 이미 삭제된 게시글 | `409 Conflict` |

---

## 8. 게시글 삭제 복원

| 항목 | 내용 |
|------|------|
| **메서드** | `PATCH` |
| **엔드포인트** | `/api/v1/admin/contents/{contentId}/restore` |

### Path Parameter

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| contentId | Long | 복원할 게시글 ID |

### 요청 예시

```
PATCH /api/v1/admin/contents/10/restore
```

### Response `200 OK`

```json
{
  "id": 10,
  "title": "관리자 테스트 게시글 10번",
  "body": "이것은 관리자가 작성한 10번째 테스트 게시글입니다.",
  "userId": "aaaaaaaa-0000-0000-0000-000000000001",
  "createdBy": "admin",
  "createdDate": "2026-04-17T09:10:00",
  "lastModifiedBy": "admin",
  "lastModifiedDate": "2026-04-17T16:35:00",
  "deletedBy": null,
  "deletedDate": null
}
```

### Error

| 상황 | 상태 코드 |
|------|-----------|
| 게시글 없음 | `404 Not Found` |
| 삭제되지 않은 게시글 | `409 Conflict` |
