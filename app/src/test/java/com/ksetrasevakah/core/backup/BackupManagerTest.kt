package com.ksetrasevakah.core.backup

import com.ksetrasevakah.core.backup.model.BackupStatus
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.dao.BackupLogDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class BackupManagerTest {

    private lateinit var database: KsetraDatabase
    private lateinit var backupLogDao: BackupLogDao
    private lateinit var driveApiClient: DriveApiClient
    private lateinit var backupManager: BackupManager

    @TempDir
    lateinit var tempDir: File

    @BeforeEach
    fun setup() {
        database = mockk(relaxed = true)
        backupLogDao = mockk(relaxed = true)
        driveApiClient = mockk()
        coEvery { backupLogDao.insert(any()) } returns 1L
        backupManager = BackupManager(database, backupLogDao, driveApiClient)
    }

    @Test
    fun `initial status is Idle`() {
        assertEquals(BackupStatus.Idle, backupManager.status.value)
    }

    @Test
    fun `createBackup returns error when db path unavailable`() = runTest {
        val mockDb = mockk<android.database.sqlite.SQLiteDatabase>()
        every { database.openHelper.writableDatabase } returns mockk {
            every { path } returns null
        }

        val result = backupManager.createBackup(tempDir)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `createBackup succeeds with valid db path`() = runTest {
        val dbFile = File(tempDir, "test.db").also { it.writeText("test data") }
        every { database.openHelper.writableDatabase } returns mockk {
            every { path } returns dbFile.absolutePath
        }

        val backupDir = File(tempDir, "backups")
        val result = backupManager.createBackup(backupDir)

        assertTrue(result is Result.Success)
        assertTrue(backupManager.status.value is BackupStatus.Success)
    }

    @Test
    fun `createBackup logs success to backup log`() = runTest {
        val dbFile = File(tempDir, "test.db").also { it.writeText("test data") }
        every { database.openHelper.writableDatabase } returns mockk {
            every { path } returns dbFile.absolutePath
        }

        backupManager.createBackup(File(tempDir, "backups"))

        coVerify { backupLogDao.insert(match { it.status == "success" && it.type == "local" }) }
    }

    @Test
    fun `resetStatus sets status back to Idle`() = runTest {
        val dbFile = File(tempDir, "test.db").also { it.writeText("data") }
        every { database.openHelper.writableDatabase } returns mockk {
            every { path } returns dbFile.absolutePath
        }

        backupManager.createBackup(File(tempDir, "backups"))
        assertTrue(backupManager.status.value is BackupStatus.Success)

        backupManager.resetStatus()
        assertEquals(BackupStatus.Idle, backupManager.status.value)
    }
}
