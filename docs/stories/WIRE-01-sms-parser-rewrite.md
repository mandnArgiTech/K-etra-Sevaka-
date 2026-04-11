# WIRE-01: Rewrite SMS Parser for Real Taro Smart Panel (3L) Format

**Severity:** 🔴 CRITICAL — Nothing works without this  
**Size:** L (4-8h)  
**Dependencies:** None (this is the foundation)

## Problem
The current `RegexSmsParser` expects `R=3.82A Y=3.71A B=3.89A V=228V` format.
The real Taro Smart Panel (3L) sends multi-line SMS with completely different structure.

### Real SMS Format (from 10-day data)
```
Taro Smart Panel (3L):          ← Header (always present)
                                ← Blank line
Attention!                      ← Severity: "Attention!" or "Alert!"
Motor Turned ON                 ← Event type
via : Keypad                    ← Source (optional: "Keypad" or "APP")
                                ← Blank line
11/04/26  11:19:25              ← Timestamp DD/MM/YY  HH:MM:SS
```

### All 11 Event Types (exhaustive from real data)
```
1. "Motor Turned ON"   + "via : Keypad|APP"
2. "Motor Turned OFF"  + "via : Keypad|APP"
3. "Power Failure"
4. "Device Powered ON" + "Motor Status :Off|On" + "Mode :Manual|Auto"
5. "Low Voltage"       + "Detected Voltage (R Y B) : 349 351 352"
6. "Dryrun"            + "Detected Current (R Y B) : 08.80 09.36 09.33"
7. "Power Resumed"
8. "R/B Phase Failure"  + "Detected Voltage (R Y B) : 015 015 015"
9. "Y Phase Failure"    + "Detected Voltage (R Y B) : 190 192 381"
10. "Overload"          + "Detected Current (R Y B) : ..."  (expected, not yet seen)
11. "Command Not Matched"
```

## Acceptance Criteria
- [ ] AC1: `TaroSmsParser` (rename from RegexSmsParser) parses ALL 11 event types
- [ ] AC2: Returns `TaroPanelEvent` (new sealed class, not `ParsedTelemetry`) with typed event data
- [ ] AC3: Extracts timestamp from SMS body: `DD/MM/YY  HH:MM:SS` → `Long` epoch millis
- [ ] AC4: Extracts `via` source: KEYPAD, APP, or null
- [ ] AC5: Extracts severity: ATTENTION or ALERT
- [ ] AC6: For Low Voltage / Phase Failure: extracts 3 voltage values as `Triple<Float, Float, Float>`
- [ ] AC7: For Dryrun / Overload: extracts 3 current values as `Triple<Float, Float, Float>`
- [ ] AC8: For Device Powered ON: extracts motorStatus (On/Off) and mode (Manual/Auto)
- [ ] AC9: Parser is 100% deterministic, zero AI, runs in < 1ms
- [ ] AC10: Unit tests cover all 11 event types with exact SMS strings from `sms_data_10_days.txt`
- [ ] AC11: Unknown/malformed SMS → `TaroPanelEvent.Unknown` with raw body (no crash)

## Normalized Output Model

```kotlin
sealed class TaroPanelEvent {
    abstract val timestamp: Long        // Parsed from SMS or fallback to receipt time
    abstract val severity: Severity     // ATTENTION or ALERT
    abstract val rawSms: String         // Original full SMS text

    enum class Severity { ATTENTION, ALERT }
    enum class Via { KEYPAD, APP }

    data class MotorOn(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String,
        val via: Via
    ) : TaroPanelEvent()

    data class MotorOff(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String,
        val via: Via
    ) : TaroPanelEvent()

    data class PowerFailure(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String
    ) : TaroPanelEvent()

    data class PowerResumed(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String
    ) : TaroPanelEvent()

    data class DevicePoweredOn(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String,
        val motorStatus: Boolean,       // true = On, false = Off
        val mode: String                // "Manual" or "Auto"
    ) : TaroPanelEvent()

    data class LowVoltage(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String,
        val voltageR: Float,
        val voltageY: Float,
        val voltageB: Float
    ) : TaroPanelEvent()

    data class Dryrun(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String,
        val currentR: Float,
        val currentY: Float,
        val currentB: Float
    ) : TaroPanelEvent()

    data class PhaseFailure(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String,
        val failedPhases: String,       // "R/B", "Y", etc.
        val voltageR: Float,
        val voltageY: Float,
        val voltageB: Float
    ) : TaroPanelEvent()

    data class Overload(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String,
        val currentR: Float,
        val currentY: Float,
        val currentB: Float
    ) : TaroPanelEvent()

    data class CommandNotMatched(
        override val timestamp: Long,
        override val severity: Severity,
        override val rawSms: String
    ) : TaroPanelEvent()

    data class Unknown(
        override val timestamp: Long,
        override val severity: Severity = Severity.ATTENTION,
        override val rawSms: String
    ) : TaroPanelEvent()
}
```

## Parser Implementation

```kotlin
object TaroSmsParser {

    fun parse(smsBody: String, receiptTimestamp: Long = System.currentTimeMillis()): TaroPanelEvent {
        val lines = smsBody.lines().map { it.trim() }.filter { it.isNotEmpty() }
        
        // Extract timestamp from last line: "11/04/26  11:19:25"
        val timestamp = parseTimestamp(lines.lastOrNull()) ?: receiptTimestamp
        
        // Extract severity
        val severity = if (lines.any { it.startsWith("Alert", ignoreCase = true) })
            TaroPanelEvent.Severity.ALERT else TaroPanelEvent.Severity.ATTENTION
        
        // Match event type by scanning lines for keywords
        val body = smsBody.uppercase()
        
        return when {
            "MOTOR TURNED ON" in body -> {
                val via = extractVia(lines)
                TaroPanelEvent.MotorOn(timestamp, severity, smsBody, via)
            }
            "MOTOR TURNED OFF" in body -> {
                val via = extractVia(lines)
                TaroPanelEvent.MotorOff(timestamp, severity, smsBody, via)
            }
            "DRYRUN" in body -> {
                val (r, y, b) = extractRYB(lines, "Current")
                TaroPanelEvent.Dryrun(timestamp, severity, smsBody, r, y, b)
            }
            "PHASE FAILURE" in body -> {
                val failedPhases = extractFailedPhases(lines) // "R/B", "Y", etc.
                val (r, y, b) = extractRYB(lines, "Voltage")
                TaroPanelEvent.PhaseFailure(timestamp, severity, smsBody, failedPhases, r, y, b)
            }
            "OVERLOAD" in body -> {
                val (r, y, b) = extractRYB(lines, "Current")
                TaroPanelEvent.Overload(timestamp, severity, smsBody, r, y, b)
            }
            "LOW VOLTAGE" in body -> {
                val (r, y, b) = extractRYB(lines, "Voltage")
                TaroPanelEvent.LowVoltage(timestamp, severity, smsBody, r, y, b)
            }
            "POWER FAILURE" in body -> TaroPanelEvent.PowerFailure(timestamp, severity, smsBody)
            "POWER RESUMED" in body -> TaroPanelEvent.PowerResumed(timestamp, severity, smsBody)
            "DEVICE POWERED ON" in body -> {
                val motorOn = lines.any { it.uppercase().contains("MOTOR STATUS :ON") }
                val mode = lines.find { it.uppercase().startsWith("MODE") }
                    ?.substringAfter(":")?.trim() ?: "Manual"
                TaroPanelEvent.DevicePoweredOn(timestamp, severity, smsBody, motorOn, mode)
            }
            "COMMAND NOT MATCHED" in body -> TaroPanelEvent.CommandNotMatched(timestamp, severity, smsBody)
            else -> TaroPanelEvent.Unknown(timestamp, rawSms = smsBody)
        }
    }

    /** Parse "DD/MM/YY  HH:MM:SS" → epoch millis */
    private fun parseTimestamp(line: String?): Long? {
        line ?: return null
        val match = TIMESTAMP_REGEX.find(line) ?: return null
        val (dd, mm, yy, hh, min, ss) = match.destructured
        return try {
            val cal = java.util.Calendar.getInstance().apply {
                set(2000 + yy.toInt(), mm.toInt() - 1, dd.toInt(), hh.toInt(), min.toInt(), ss.toInt())
                set(java.util.Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        } catch (_: Exception) { null }
    }

    /** Extract "via : Keypad" or "via : APP" */
    private fun extractVia(lines: List<String>): TaroPanelEvent.Via {
        val viaLine = lines.find { it.uppercase().startsWith("VIA") } ?: return TaroPanelEvent.Via.KEYPAD
        return if (viaLine.uppercase().contains("APP")) TaroPanelEvent.Via.APP
        else TaroPanelEvent.Via.KEYPAD
    }

    /** Extract "Detected Voltage (R Y B) : 349 351 352" or "Detected Current (R Y B) : 08.80 09.36 09.33" */
    private fun extractRYB(lines: List<String>, type: String): Triple<Float, Float, Float> {
        val line = lines.find { it.uppercase().contains("DETECTED ${type.uppercase()}") }
            ?: return Triple(0f, 0f, 0f)
        val values = line.substringAfter(":").trim().split("\\s+".toRegex())
        return Triple(
            values.getOrNull(0)?.toFloatOrNull() ?: 0f,
            values.getOrNull(1)?.toFloatOrNull() ?: 0f,
            values.getOrNull(2)?.toFloatOrNull() ?: 0f
        )
    }

    /** Extract "R/B" from "R/B Phase Failure" or "Y" from "Y Phase Failure" */
    private fun extractFailedPhases(lines: List<String>): String {
        val line = lines.find { it.uppercase().contains("PHASE FAILURE") } ?: return "?"
        return line.substringBefore("Phase", "?").trim()
    }

    private val TIMESTAMP_REGEX = Regex("""(\d{2})/(\d{2})/(\d{2})\s+(\d{2}):(\d{2}):(\d{2})""")
}
```

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/sms/TaroSmsParser.kt
app/src/main/java/com/ksetrasevakah/core/sms/model/TaroPanelEvent.kt
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/ai/SmsTelemetryProcessor.kt  ← use TaroSmsParser
app/src/main/java/com/ksetrasevakah/core/sms/RegexSmsParser.kt        ← DELETE or keep as fallback
```

## Test Requirements — Use EXACT strings from sms_data_10_days.txt

```kotlin
class TaroSmsParserTest {

    @Test fun `Motor Turned ON via Keypad`() {
        val sms = "Taro Smart Panel (3L):\n\nAttention!\nMotor Turned ON\nvia : Keypad\n\n11/04/26  11:19:25"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.MotorOn>(event)
        assertEquals(TaroPanelEvent.Via.KEYPAD, event.via)
        assertEquals(TaroPanelEvent.Severity.ATTENTION, event.severity)
        // timestamp = 2026-04-11 11:19:25 IST
    }

    @Test fun `Motor Turned OFF via APP`() {
        val sms = "Taro Smart Panel (3L):\n\nAttention!\nMotor Turned OFF\nvia : APP\n\n10/04/26  05:51:03"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.MotorOff>(event)
        assertEquals(TaroPanelEvent.Via.APP, event.via)
    }

    @Test fun `Low Voltage with RYB values`() {
        val sms = "Taro Smart Panel (3L):\n\nAlert!\nLow Voltage\nDetected Voltage (R Y B) : 349 351 352\n\n11/04/26  08:49:27"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.LowVoltage>(event)
        assertEquals(349f, event.voltageR)
        assertEquals(351f, event.voltageY)
        assertEquals(352f, event.voltageB)
    }

    @Test fun `Dryrun with current values`() {
        val sms = "Taro Smart Panel (3L):\n\nAlert!\nDryrun\nDetected Current (R Y B) : 08.80 09.36 09.33\n\n10/04/26  14:32:43"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.Dryrun>(event)
        assertEquals(8.80f, event.currentR)
        assertEquals(9.36f, event.currentY)
        assertEquals(9.33f, event.currentB)
    }

    @Test fun `Y Phase Failure with voltages`() {
        val sms = "Taro Smart Panel (3L):\n\nAlert!\nY Phase Failure\nDetected Voltage (R Y B) : 190 192 381\n\n03/04/26  10:13:21"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.PhaseFailure>(event)
        assertEquals("Y", event.failedPhases)
        assertEquals(190f, event.voltageR)
        assertEquals(192f, event.voltageY)
        assertEquals(381f, event.voltageB)
    }

    @Test fun `Device Powered ON with motor status`() {
        val sms = "Taro Smart Panel (3L):\n\nAttention !\nDevice Powered ON\n\nMotor Status :Off\nMode :Manual\n\n11/04/26  10:58:25"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.DevicePoweredOn>(event)
        assertEquals(false, event.motorStatus)
        assertEquals("Manual", event.mode)
    }

    @Test fun `Power Failure`() {
        val sms = "Taro Smart Panel (3L):\n\nAlert!\nPower Failure\n\n11/04/26  10:44:00"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.PowerFailure>(event)
        assertEquals(TaroPanelEvent.Severity.ALERT, event.severity)
    }

    @Test fun `Power Resumed`() {
        val sms = "Taro Smart Panel (3L):\n\nPower Resumed\n\n10/04/26  08:07:29"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.PowerResumed>(event)
    }

    @Test fun `Command Not Matched`() {
        val sms = "Taro Smart Panel (3L):\n\nCommand Not Matched\n\n10/04/26  02:16:58"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.CommandNotMatched>(event)
    }

    @Test fun `Unknown SMS format`() {
        val sms = "Some random text message"
        val event = TaroSmsParser.parse(sms)
        assertIs<TaroPanelEvent.Unknown>(event)
    }

    @Test fun `Timestamp parsing DD MM YY HH MM SS`() {
        val sms = "Taro Smart Panel (3L):\n\nAlert!\nPower Failure\n\n11/04/26  10:44:00"
        val event = TaroSmsParser.parse(sms)
        // 2026-04-11 10:44:00 IST
        assertTrue(event.timestamp > 0)
        assertTrue(event.timestamp < System.currentTimeMillis() + 86400000)
    }
}
```

## Definition of Done
- Every SMS in `sms_data_10_days.txt` parses to the correct event type
- All 11 event types tested with real data
- Parser runs in < 1ms per SMS
- No AI dependency
