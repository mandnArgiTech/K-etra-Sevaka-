# E08-S02: AI Analyst Chat Screen UI

**Epic:** 08 — AI Chat  
**Size:** L (4-8h)  
**Dependencies:** E08-S01, E05-S01, E01-S02

## Description
Full chat interface: message bubbles (user right-aligned green, AI left-aligned dark), text input bar with send + mic buttons, typing indicator with "ANALYZING" animation, thread drawer for history.

## Acceptance Criteria
- [ ] AC1: Chat screen shows header with "AI Analyst" title, "3B Orchestrator • Prediction Engine" subtitle
- [ ] AC2: Messages scroll vertically, newest at bottom, auto-scroll on new message
- [ ] AC3: User messages: right-aligned, green gradient bubble
- [ ] AC4: AI messages: left-aligned, dark card bubble with purple "PUMPIQ AI" label + brain icon
- [ ] AC5: Input bar at bottom: text field + mic button + green send button
- [ ] AC6: Send button dispatches message → shows in chat → triggers AI response
- [ ] AC7: Typing indicator: purple "ANALYZING" text + 3 pulsing dots while AI generating
- [ ] AC8: Thread drawer: hamburger menu → slide-out panel listing past threads with date + preview
- [ ] AC9: Tapping a thread in drawer loads that thread's messages
- [ ] AC10: `initialQuery` navigation param → auto-sends that query on screen open
- [ ] AC11: Empty state: welcome message from AI on new thread
- [ ] AC12: Keyboard opens → chat scrolls up, input stays visible

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/
├── ChatScreen.kt
├── ChatViewModel.kt
├── model/ChatUiState.kt
├── model/ChatUiEvent.kt
├── component/
│   ├── MessageBubble.kt
│   ├── TypingIndicator.kt
│   ├── ChatInputBar.kt
│   └── ThreadDrawer.kt

app/src/test/java/com/ksetrasevakah/feature/pumpiq/chat/ChatViewModelTest.kt
app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/chat/ChatScreenTest.kt
```

## Test Requirements
### ChatViewModelTest
- Send message → added to message list, typing = true
- AI response received → added to list, typing = false
- Initial query → auto-dispatched on init
- Load thread → messages populated from repository

### ChatScreenTest
- User message bubble right-aligned
- AI message bubble left-aligned with "PUMPIQ AI" label
- Send button tappable → message appears
- Typing indicator visible during AI processing
- Thread drawer opens on menu tap
