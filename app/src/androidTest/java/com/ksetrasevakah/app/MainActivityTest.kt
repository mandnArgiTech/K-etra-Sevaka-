package com.ksetrasevakah.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun activityLaunchesWithoutCrash() {
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun composeContentIsSet() {
        composeTestRule.onRoot().assertExists()
    }
}
