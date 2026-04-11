package com.ksetrasevakah.core.sms

import android.content.Context
import android.provider.Telephony
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads historical SMS from the device inbox.
 * [readPanelMessages] filters to the configured panel number (stored in prefs).
 * [readAllInboxSenders] returns every unique sender so the user can pick the right one.
 * Requires [android.Manifest.permission.READ_SMS].
 */
@Singleton
class SmsHistoryReader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferencesRepository
) {

    /** Returns up to [limit] messages from the configured panel number (newest first). */
    suspend fun readPanelMessages(limit: Int = 500): List<PanelSms> = withContext(Dispatchers.IO) {
        val normalizedPanel = appPreferences.getPanelNumber().replace("\\s".toRegex(), "")
        readMatchingMessages(normalizedPanel, limit)
    }

    /**
     * Same as [readPanelMessages] but filters by an explicit [number] instead of
     * the stored preference.  Used by ConnectivityCheckViewModel to preview counts.
     */
    suspend fun readMessagesFromNumber(number: String, limit: Int = 500): List<PanelSms> =
        withContext(Dispatchers.IO) {
            val normalizedPanel = number.replace("\\s".toRegex(), "")
            readMatchingMessages(normalizedPanel, limit)
        }

    /**
     * Scans the inbox (up to [scanLimit] rows) and returns every unique sender
     * with aggregate stats.  Used for the number-picker UI.
     */
    suspend fun readAllInboxSenders(scanLimit: Int = 2000): List<SenderInfo> =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val uri = Telephony.Sms.Inbox.CONTENT_URI
            val projection = arrayOf(
                Telephony.Sms.ADDRESS,
                Telephony.Sms.DATE,
                Telephony.Sms.BODY
            )
            val sort = "${Telephony.Sms.DATE} DESC"

            val senderMap = LinkedHashMap<String, SenderInfo>()
            resolver.query(uri, projection, null, null, sort)?.use { cursor ->
                val idxAddr = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val idxDate = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
                val idxBody = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
                var checked = 0
                while (cursor.moveToNext() && checked < scanLimit) {
                    checked++
                    val addr = cursor.getString(idxAddr) ?: continue
                    val ts = cursor.getLong(idxDate)
                    val body = cursor.getString(idxBody) ?: ""
                    val norm = addr.replace("\\s".toRegex(), "")
                    val existing = senderMap[norm]
                    if (existing == null) {
                        senderMap[norm] = SenderInfo(
                            address = addr,
                            normalizedAddress = norm,
                            messageCount = 1,
                            oldestMessageMs = ts,
                            newestMessageMs = ts,
                            snippet = body.take(120)
                        )
                    } else {
                        senderMap[norm] = existing.copy(
                            messageCount = existing.messageCount + 1,
                            oldestMessageMs = minOf(existing.oldestMessageMs, ts),
                            newestMessageMs = maxOf(existing.newestMessageMs, ts)
                        )
                    }
                }
            }
            senderMap.values.sortedByDescending { it.messageCount }
        }

    private fun readMatchingMessages(normalizedPanel: String, limit: Int): List<PanelSms> {
        val resolver = context.contentResolver
        val uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(
            Telephony.Sms.DATE,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY
        )
        val sort = "${Telephony.Sms.DATE} DESC"
        val out = mutableListOf<PanelSms>()
        resolver.query(uri, projection, null, null, sort)?.use { cursor ->
            val idxDate = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
            val idxAddr = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val idxBody = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
            while (cursor.moveToNext() && out.size < limit) {
                val address = cursor.getString(idxAddr) ?: continue
                val normalizedSender = address.replace("\\s".toRegex(), "")
                if (!normalizedSender.endsWith(normalizedPanel) &&
                    !normalizedPanel.endsWith(normalizedSender)
                ) {
                    continue
                }
                val ts = cursor.getLong(idxDate)
                val body = cursor.getString(idxBody) ?: continue
                out.add(PanelSms(timestamp = ts, body = body))
            }
        }
        return out
    }

    data class SenderInfo(
        val address: String,
        val normalizedAddress: String,
        val messageCount: Int,
        val oldestMessageMs: Long,
        val newestMessageMs: Long,
        val snippet: String
    )

    data class PanelSms(val timestamp: Long, val body: String)
}
