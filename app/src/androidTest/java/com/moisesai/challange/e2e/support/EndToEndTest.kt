package com.moisesai.challange.e2e.support

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.moisesai.challange.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

/**
 * The base of every journey: the whole app on the emulator, talking to the [FakeBackend] and playing on the
 * [FakePlayerController], with an empty database. The backend and the database are ready before the app opens.
 */
@HiltAndroidTest
abstract class EndToEndTest {

    private val hiltRule = HiltAndroidRule(this)
    protected val composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> =
        createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: TestRule = RuleChain.outerRule(hiltRule).around(FreshStateRule()).around(composeRule)

    @Inject
    lateinit var player: FakePlayerController

    protected val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun injectDependencies() = hiltRule.inject()

    protected fun text(stringId: Int): String = context.getString(stringId)

    /** Waits for a node with this text; the splash and the first answer of the backend take a few seconds. */
    protected fun waitForText(text: String) = composeRule.waitUntil(TIMEOUT_MILLIS) {
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }

    /** Waits for a node whose text contains [part]. */
    protected fun waitForTextContaining(part: String) = composeRule.waitUntil(TIMEOUT_MILLIS) {
        composeRule.onAllNodesWithText(part, substring = true).fetchSemanticsNodes().isNotEmpty()
    }

    protected fun countTextContaining(part: String): Int =
        composeRule.onAllNodesWithText(part, substring = true).fetchSemanticsNodes().size

    protected fun waitForDescription(description: String) = composeRule.waitUntil(TIMEOUT_MILLIS) {
        composeRule.onAllNodesWithContentDescription(description).fetchSemanticsNodes().isNotEmpty()
    }

    protected fun waitUntilTextIsGone(text: String) = composeRule.waitUntil(TIMEOUT_MILLIS) {
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
    }

    protected fun waitForNode(matcher: SemanticsMatcher) = composeRule.waitUntil(TIMEOUT_MILLIS) {
        composeRule.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
    }

    private class FreshStateRule : ExternalResource() {
        override fun before() {
            FakeBackend.start()
            FakeBackend.reset()
            ApplicationProvider.getApplicationContext<Context>().deleteDatabase(DATABASE_NAME)
        }
    }

    protected companion object {
        const val TIMEOUT_MILLIS = 20_000L
        private const val DATABASE_NAME = "moisesai.db"
    }
}
