# Taro Smart Panel (3L) — SMS Format Reference

> Exhaustive catalog derived from 156 real SMS messages over 10 days (01/04/26 – 11/04/26).
> Every SMS parser test case should use EXACT strings from this document.

## Message Structure (all messages follow this pattern)

```
Taro Smart Panel (3L):    ← Header (always "Taro Smart Panel (3L):")
                          ← Blank line
{Severity}                ← "Attention!" or "Alert!" or none (Power Resumed)
{Event Line}              ← Event identifier
{Data Lines}              ← Optional: voltage, current, motor status, via source
                          ← Blank line
{DD/MM/YY  HH:MM:SS}     ← Timestamp (always last non-empty line)
```

**NOTE:** Severity has inconsistent spacing: sometimes "Attention!" sometimes "Attention !" (with space before !). Parser must handle both.

---

## Event Type 1: Motor Turned ON

**Count:** 52 (49 Keypad + 3 APP)  
**Severity:** Attention!

```
Taro Smart Panel (3L):

Attention!
Motor Turned ON
via : Keypad

11/04/26  11:19:25
```

```
Taro Smart Panel (3L):

Attention!
Motor Turned ON
via : APP

10/04/26  05:50:00
```

**Extract:** eventType=MOTOR_ON, source=KEYPAD|APP, timestamp

---

## Event Type 2: Motor Turned OFF

**Count:** 12 (11 Keypad + 1 APP)  
**Severity:** Attention!

```
Taro Smart Panel (3L):

Attention!
Motor Turned OFF
via : Keypad

11/04/26  08:36:22
```

```
Taro Smart Panel (3L):

Attention!
Motor Turned OFF
via : APP

10/04/26  05:51:03
```

**Extract:** eventType=MOTOR_OFF, source=KEYPAD|APP, timestamp

---

## Event Type 3: Power Failure

**Count:** 34  
**Severity:** Alert!

```
Taro Smart Panel (3L):

Alert!
Power Failure

11/04/26  10:44:00
```

**Extract:** eventType=POWER_FAILURE, timestamp  
**No data lines.**

---

## Event Type 4: Device Powered ON

**Count:** 33  
**Severity:** Attention ! (note the space before !)

```
Taro Smart Panel (3L):

Attention !
Device Powered ON

Motor Status :Off
Mode :Manual

11/04/26  10:58:25
```

**Extract:** eventType=DEVICE_POWERED_ON, motorStatus=false, mode="Manual", timestamp  
**NOTE:** Motor Status can be `:Off` or `:On`. Mode can be `:Manual` or `:Auto`.

---

## Event Type 5: Low Voltage

**Count:** 24  
**Severity:** Alert!

```
Taro Smart Panel (3L):

Alert!
Low Voltage
Detected Voltage (R Y B) : 349 351 352

11/04/26  08:49:27
```

**Extract:** eventType=LOW_VOLTAGE, voltageR=349, voltageY=351, voltageB=352, timestamp  
**Voltage range observed:** 344–353V per phase (this is a 3-phase 415V system)

---

## Event Type 6: Dryrun

**Count:** 3  
**Severity:** Alert!

```
Taro Smart Panel (3L):

Alert!
Dryrun
Detected Current (R Y B) : 08.80 09.36 09.33

10/04/26  14:32:43
```

**Extract:** eventType=DRYRUN, currentR=8.80, currentY=9.36, currentB=9.33, timestamp  
**NOTE:** Current values have 2 decimal places. All 3 observed dryruns had identical current values.

---

## Event Type 7: Power Resumed

**Count:** 1  
**Severity:** None (no "Attention!" or "Alert!" prefix)

```
Taro Smart Panel (3L):

Power Resumed

10/04/26  08:07:29
```

**Extract:** eventType=POWER_RESUMED, timestamp  
**NOTE:** No severity prefix — unique among all event types.

---

## Event Type 8: R/B Phase Failure

**Count:** 1  
**Severity:** Alert!

```
Taro Smart Panel (3L):

Alert!
R/B Phase Failure
Detected Voltage (R Y B) : 015 015 015

05/04/26  10:30:27
```

**Extract:** eventType=PHASE_FAILURE, failedPhases="R/B", voltageR=15, voltageY=15, voltageB=15, timestamp  
**NOTE:** Voltage 015 = near-zero, indicating total phase loss.

---

## Event Type 9: Y Phase Failure

**Count:** 2  
**Severity:** Alert!

```
Taro Smart Panel (3L):

Alert!
Y Phase Failure
Detected Voltage (R Y B) : 190 192 381

03/04/26  10:13:21
```

**Extract:** eventType=PHASE_FAILURE, failedPhases="Y", voltageR=190, voltageY=192, voltageB=381, timestamp  
**NOTE:** Y phase at 190V vs normal 350V — clear Y-phase failure. B phase at 381V (over-voltage on surviving phase).

---

## Event Type 10: Overload (expected but not seen in 10-day data)

**Expected format (based on Taro documentation pattern):**

```
Taro Smart Panel (3L):

Alert!
Overload
Detected Current (R Y B) : 12.50 13.10 12.80

XX/XX/XX  XX:XX:XX
```

**Extract:** eventType=OVERLOAD, currentR, currentY, currentB, timestamp

---

## Event Type 11: Command Not Matched

**Count:** 6  
**Severity:** None

```
Taro Smart Panel (3L):

Command Not Matched

10/04/26  02:16:58
```

**Extract:** eventType=COMMAND_NOT_MATCHED, timestamp  
**NOTE:** Indicates Taro panel received an SMS command it couldn't understand.

---

## Timestamp Format

**Pattern:** `DD/MM/YY  HH:MM:SS` (note: TWO spaces between date and time)  
**Timezone:** Indian Standard Time (IST, UTC+5:30) — hardcode or use device timezone  
**Year:** YY format, always 2000+YY. Current year in data: 26 = 2026  
**Examples:**
```
11/04/26  11:19:25  →  2026-04-11 11:19:25 IST
01/04/26  15:30:34  →  2026-04-01 15:30:34 IST
```

---

## Summary Statistics (10 days)

| Event Type | Count | % of Total |
|-----------|-------|-----------|
| Motor Turned ON | 52 | 33.3% |
| Power Failure | 34 | 21.8% |
| Device Powered ON | 33 | 21.2% |
| Low Voltage | 24 | 15.4% |
| Motor Turned OFF | 12 | 7.7% |
| Command Not Matched | 6 | 3.8% |
| Dryrun | 3 | 1.9% |
| Y Phase Failure | 2 | 1.3% |
| Power Resumed | 1 | 0.6% |
| R/B Phase Failure | 1 | 0.6% |
| **Total** | **156** | **100%** |

## Key Observations for Prediction Engine
1. **40 Motor ON vs 12 Motor OFF** — many ON events are retries after low voltage trips (motor auto-trips, worker restarts immediately). The motor doesn't always get a corresponding OFF event.
2. **Power failures cluster at night (01:00-03:00) and morning (07:00-09:00)** — grid instability during peak/transition hours.
3. **Low voltage is a PRECURSOR to power failure** — often appears 1-5 minutes before failure.
4. **Worker start time: 07:27–10:59** — wide range, but most starts between 08:00-09:00.
5. **Dryruns all at 14:32** — strong afternoon time correlation, likely water table drop.
6. **52 Motor ON / 12 Motor OFF imbalance** — most OFFs are implicit (power failure → device powered on → motor status off).
