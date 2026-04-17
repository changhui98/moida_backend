# User API 명세서

> Base URL: `http://localhost:8080/api/v1/users`
> 모든 엔드포인트 인증 필요 (`Authorization: Bearer {token}`)

---

## 1. 내 프로필 조회

| 항목 | 내용 |
|------|------|
| **메서드** | `GET` |
| **엔드포인트** | `/api/v1/users/me` |

### Response `200 OK`

```json
{
  "id": "aaaaaaaa-0000-0000-0000-000000000002",
  "username": "user1",
  "nickname": "유저일",
  "userEmail": "user1@moida.com",
  "address": "서울 마포구 합정동",
  "role": "USER",
  "createdAt": "2026-04-17T10:00:00",
  "modifiedAt": "2026-04-17T10:00:00"
}
```

---

## 2. 내 프로필 수정

| 항목 | 내용 |
|------|------|
| **메서드** | `PATCH` |
| **엔드포인트** | `/api/v1/users/me` |

### Request Body

```json
{
  "nickname": "새닉네임",
  "userEmail": "new@moida.com",
  "address": "서울 용산구 이태원동",
  "currentPassword": "Test1234!",
  "newPassword": "NewPass5678!"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| nickname | String | 변경할 닉네임 |
| userEmail | String | 변경할 이메일 |
| address | String | 변경할 주소 |
| currentPassword | String | 현재 비밀번호 (비밀번호 변경 시 필요) |
| newPassword | String | 새 비밀번호 |

### Response `200 OK`

```json
{
  "id": "aaaaaaaa-0000-0000-0000-000000000002",
  "username": "user1",
  "nickname": "새닉네임",
  "userEmail": "new@moida.com",
  "address": "서울 용산구 이태원동",
  "role": "USER",
  "createdAt": "2026-04-17T10:00:00",
  "modifiedAt": "2026-04-17T15:30:00"
}
```

---

## 3. 회원 탈퇴

| 항목 | 내용 |
|------|------|
| **메서드** | `DELETE` |
| **엔드포인트** | `/api/v1/users/me` |

### Response `204 No Content`

- Body 없음

---

## 4. 유저 목록 조회

| 항목 | 내용 |
|------|------|
| **메서드** | `GET` |
| **엔드포인트** | `/api/v1/users` |

### Query Parameters

| 파라미터 | 타입 | 기본값 | 설명 |
|----------|------|--------|------|
| page | int | 0 | 페이지 번호 (0부터 시작) |
| size | int | 10 | 페이지당 항목 수 |

### 요청 예시

```
GET /api/v1/users?page=0&size=10
```

### Response `200 OK`

```json
{
  "content": [
    {
      "id": "aaaaaaaa-0000-0000-0000-000000000001",
      "username": "admin",
      "nickname": "관리자",
      "userEmail": "admin@moida.com",
      "address": "서울 강남구 삼성동"
    },
    {
      "id": "aaaaaaaa-0000-0000-0000-000000000002",
      "username": "user1",
      "nickname": "유저일",
      "userEmail": "user1@moida.com",
      "address": "서울 마포구 합정동"
    },
    {
      "id": "aaaaaaaa-0000-0000-0000-000000000003",
      "username": "user2",
      "nickname": "유저이",
      "userEmail": "user2@moida.com",
      "address": "서울 송파구 잠실동"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 4,
  "totalPages": 1,
  "hasNext": false
}
```
