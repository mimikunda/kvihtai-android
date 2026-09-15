# Contributing to KvihtAI Android

This repository contains the native Android client. It handles local network discovery, real-time WebSocket telemetry rendering, and session persistence. 

## 1. Architectural Boundaries
- **UI Toolkit:** Strictly use Jetpack Compose. XML layouts are prohibited.
- **Design Language:** Implement Material 3 Expressive guidelines. Use dynamic theming where applicable.
- **Separation of Concerns:** Keep network handling (Ktor), local database (Room), and UI logic completely decoupled. Use ViewModels to expose state flows to Compose.
- **Canvas Rendering:** High-framerate trajectory plotting must be done via optimized Compose Canvas routines. Avoid unnecessary recompositions.

## 2. Assisted Synthesis & LLM Usage
If utilizing AI models to generate Android components:
1. **State Management:** LLMs frequently hallucinate anti-patterns in Compose state hoisting. Ensure state flows linearly (events up, state down).
2. **Network Resilience:** Generated networking code assumes perfect conditions. You must manually implement disconnect handling, reconnect backoffs, and error states for the local Pi network.
3. **Review:** Blindly pasting generated UI code leads to monolithic composables. Break down layouts into small, reusable components.

## 3. Git & Branching Protocol
1. **Branch Naming:** `feat/ui-<screen>`, `feat/net-<feature>`, `fix/<bug>`.
2. **Commit Frequency:** Commit per logical UI component or data layer implementation.
3. **PR Merging:** All PRs must compile cleanly in Android Studio and pass basic linting before review.

## 4. Local Testing
- During early development, test the UI against mock JSON data matching `API_CONTRACT.md` before attempting to pair with the physical edge hardware.
