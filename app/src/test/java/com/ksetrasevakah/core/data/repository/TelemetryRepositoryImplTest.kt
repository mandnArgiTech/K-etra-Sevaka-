package com.ksetrasevakah.core.data.repository

import app.cash.turbine.test
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.TelemetryDao
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TelemetryRepositoryImplTest {

    private lateinit var dao: TelemetryDao
    private lateinit var repository: TelemetryRepositoryImpl

    @BeforeEach
    fun setup() {
        dao = mockk(relaxed = true)
        repository = TelemetryRepositoryImpl(dao)
    }

    private fun telemetryEntity(id: Long = 1L, voltage: Float? = 230f) = TelemetryEntity(
        id = id,
        rawSms = "test sms",
        timestamp = System.currentTimeMillis(),
        motorOn = true,
        voltage = voltage,
    )

    @Test
    fun `observeRecent emits Success with telemetry list`() = runTest {
        val entities = listOf(telemetryEntity(1L), telemetryEntity(2L))
        every { dao.getRecent(any()) } returns flowOf(entities)

        repository.observeRecent(7).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(2, (result as Result.Success).data.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeRecent emits Success with empty list`() = runTest {
        every { dao.getRecent(any()) } returns flowOf(emptyList())

        repository.observeRecent(7).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertTrue((result as Result.Success).data.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getRecentList returns Success on happy path`() = runTest {
        val entities = listOf(telemetryEntity())
        coEvery { dao.getRecentList(any()) } returns entities

        val result = repository.getRecentList(7)
        assertTrue(result.isSuccess)
        assertEquals(1, (result as Result.Success).data.size)
    }

    @Test
    fun `getRecentList returns Error when DAO throws`() = runTest {
        coEvery { dao.getRecentList(any()) } throws RuntimeException("DB error")

        val result = repository.getRecentList(7)
        assertTrue(result.isError)
        assertEquals("DB error", (result as Result.Error).message)
    }

    @Test
    fun `getAvgVoltage returns Success with value`() = runTest {
        coEvery { dao.getAvgVoltage(any(), any()) } returns 225.5f

        val result = repository.getAvgVoltage(1000L, 2000L)
        assertTrue(result.isSuccess)
        assertEquals(225.5f, (result as Result.Success).data)
    }

    @Test
    fun `getAvgVoltage returns Success with null when no data`() = runTest {
        coEvery { dao.getAvgVoltage(any(), any()) } returns null

        val result = repository.getAvgVoltage(1000L, 2000L)
        assertTrue(result.isSuccess)
        assertNull((result as Result.Success).data)
    }

    @Test
    fun `getAvgVoltage returns Error when DAO throws`() = runTest {
        coEvery { dao.getAvgVoltage(any(), any()) } throws RuntimeException("DB error")

        val result = repository.getAvgVoltage(1000L, 2000L)
        assertTrue(result.isError)
    }

    @Test
    fun `insert returns Success with row id`() = runTest {
        val entity = telemetryEntity()
        coEvery { dao.insert(entity) } returns 42L

        val result = repository.insert(entity)
        assertTrue(result.isSuccess)
        assertEquals(42L, (result as Result.Success).data)
        coVerify { dao.insert(entity) }
    }

    @Test
    fun `insert returns Error when DAO throws`() = runTest {
        val entity = telemetryEntity()
        coEvery { dao.insert(entity) } throws RuntimeException("DB error")

        val result = repository.insert(entity)
        assertTrue(result.isError)
        assertEquals("DB error", (result as Result.Error).message)
    }
}
