# WIRE Stories — End-to-End Wiring & Data Pipeline Fixes

> These stories fix the fundamental data pipeline: real SMS parsing, DB schema alignment,
> motor state reactivity, prediction engine wiring, dashboard data binding, and chat connectivity.
> 
> **Root Cause:** The SMS format from the real Taro Smart Panel (3L) is completely different
> from what the parser expects. Nothing downstream works because nothing gets parsed correctly upstream.

## Problem Summary

The real Taro SMS format is multi-line with a header, event type, optional data, and timestamp:
```
Taro Smart Panel (3L):

Attention!
Motor Turned ON
via : Keypad

11/04/26  11:19:25
```

The current `RegexSmsParser` expects single-line `R=3.82A Y=3.71A` format. **Zero fields match.**
Everything downstream — motor state, predictions, dashboard, chat — is empty because the source data
never gets parsed.

## Event Types Discovered (10 days, 156 messages)

| Event Type | Count | Has Data? | Current Parser Handles? |
|-----------|-------|-----------|------------------------|
| Motor Turned ON (via Keypad/APP) | 49+3 | via source | ❌ Partially (matches "MOTOR ON" but misses `via` and timestamp) |
| Motor Turned OFF (via Keypad/APP) | 12 | via source | ❌ No (doesn't set motorOn=false) |
| Power Failure | 34 | — | ❌ No |
| Device Powered ON | 33 | Motor Status, Mode | ❌ No |
| Low Voltage | 24 | Voltage R Y B | ❌ No (regex expects `V=` not `(R Y B) : 349 351 352`) |
| Dryrun | 3 | Current R Y B | ❌ No (regex expects `R=` not `(R Y B) : 08.80 09.36 09.33`) |
| Power Resumed | 1 | — | ❌ No |
| R/B Phase Failure | 1 | Voltage R Y B | ❌ No |
| Y Phase Failure | 2 | Voltage R Y B | ❌ No |
| Command Not Matched | 6 | — | ❌ No |

---

| Story | Title | Est. |
|-------|-------|------|
| [WIRE-01](./WIRE-01-sms-parser-rewrite.md) | Rewrite SMS parser for real Taro Smart Panel format | L |
| [WIRE-02](./WIRE-02-normalized-event-model.md) | Normalized event model + DB schema alignment | L |
| [WIRE-03](./WIRE-03-ingestion-pipeline.md) | Rewire ingestion pipeline: SMS → parse → DB + faults + motor state + worker activity | L |
| [WIRE-04](./WIRE-04-motor-state-reactive.md) | Fix motor state reactivity: SMS events drive dashboard status | M |
| [WIRE-05](./WIRE-05-prediction-engine-real-data.md) | Wire prediction engine to real parsed data | L |
| [WIRE-06](./WIRE-06-dashboard-data-binding.md) | Fix dashboard data binding: charts, summary, prediction cards | L |
| [WIRE-07](./WIRE-07-chat-assistant-wiring.md) | Wire chat assistant: system prompt with real data, response generation | L |
| [WIRE-08](./WIRE-08-vector-db-strategy.md) | Vector DB strategy: what to embed, when, and how to query | M |

**Total: 8 stories, ~40-50 hours**
