# E08-S01: Chat Threads & Messages Data Layer

**Epic:** 08 — AI Chat  
**Size:** M (2-4h)  
**Dependencies:** E02-S01

## Description
Data layer for AI Analyst chat: thread management (create, list, delete) and message persistence (insert, paginate, observe).

## Acceptance Criteria
- [ ] AC1: `ChatThread` domain model with id, title, createdAt, lastMessageAt, preview
- [ ] AC2: `ChatMessage` domain model with id, threadId, role (USER/AI), text, timestamp
- [ ] AC3: `ChatRepository.getThreads()` returns `Flow<List<ChatThread>>` sorted by lastMessageAt DESC
- [ ] AC4: `ChatRepository.getMessages(threadId, limit, offset)` returns paginated messages
- [ ] AC5: `ChatRepository.addMessage(threadId, message)` inserts and updates thread's lastMessageAt
- [ ] AC6: `ChatRepository.createThread(title)` creates new thread, returns id
- [ ] AC7: `ChatRepository.deleteThread(threadId)` cascades to messages
- [ ] AC8: Auto-generate thread title from first user message (truncated to 40 chars)
- [ ] AC9: Unit tests with mocked DAOs pass

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/domain/model/
├── ChatThread.kt
└── ChatMessage.kt

app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/domain/usecase/
├── GetChatThreadsUseCase.kt
├── GetChatMessagesUseCase.kt
├── SendChatMessageUseCase.kt
└── CreateChatThreadUseCase.kt

app/src/test/java/com/ksetrasevakah/feature/pumpiq/chat/domain/usecase/
├── GetChatThreadsUseCaseTest.kt
├── SendChatMessageUseCaseTest.kt
└── CreateChatThreadUseCaseTest.kt
```

## Test Requirements
- Create thread → appears in thread list
- Add message → thread's lastMessageAt updated
- Paginate messages → correct limit/offset
- Delete thread → messages cascade deleted
- Auto-title from "When will worker turn ON tomorrow" → "When will worker turn ON tomorrow"
- Auto-title from long message → truncated to 40 chars + "..."
