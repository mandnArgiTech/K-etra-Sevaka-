package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.model.CameraCount
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ActivitySpikeDetectorTest {

    private lateinit var repository: SecurityEventRepository
    private lateinit var detector: ActivitySpikeDetector

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        detector = ActivitySpikeDetector(repository)
    }

    @Test
    fun `isActivitySpike returns true when count exceeds threshold`() = runTest {
        coEvery { repository.getActivitySpikes(any(), any()) } returns Result.Success(
            listOf(CameraCount("Front Door", 5))
        )

        val result = detector.isActivitySpike("Front Door", System.currentTimeMillis())

        assertTrue(result)
    }

    @Test
    fun `isActivitySpike returns false when count below threshold`() = runTest {
        coEvery { repository.getActivitySpikes(any(), any()) } returns Result.Success(
            listOf(CameraCount("Front Door", 1))
        )

        val result = detector.isActivitySpike("Front Door", System.currentTimeMillis())

        assertFalse(result)
    }

    @Test
    fun `isActivitySpike returns false when camera not found`() = runTest {
        coEvery { repository.getActivitySpikes(any(), any()) } returns Result.Success(
            listOf(CameraCount("Other Camera", 5))
        )

        val result = detector.isActivitySpike("Front Door", System.currentTimeMillis())

        assertFalse(result)
    }

    @Test
    fun `isCoordinatedActivity returns true when multiple cameras active`() = runTest {
        coEvery { repository.getActivitySpikes(any(), any()) } returns Result.Success(
            listOf(
                CameraCount("Front Door", 3),
                CameraCount("Backyard", 2)
            )
        )

        val result = detector.isCoordinatedActivity(System.currentTimeMillis())

        assertTrue(result)
    }

    @Test
    fun `isCoordinatedActivity returns false with single active camera`() = runTest {
        coEvery { repository.getActivitySpikes(any(), any()) } returns Result.Success(
            listOf(
                CameraCount("Front Door", 3),
                CameraCount("Backyard", 1)
            )
        )

        val result = detector.isCoordinatedActivity(System.currentTimeMillis())

        assertFalse(result)
    }
}
