package com.rjial.storybook.ui.authentication.login

import android.view.View
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedDiagnosingMatcher

import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.rjial.storybook.R
import com.rjial.storybook.network.response.StoryAuthRegisterBody
import com.rjial.storybook.ui.authentication.register.RegisterAuthActivity
import com.rjial.storybook.ui.main.MainActivity
import com.rjial.storybook.ui.main.adapter.StoryListAdapter
import com.rjial.storybook.ui.story.add.AddStoryActivity
import com.rjial.storybook.util.EspressoIdlingResource
import okhttp3.internal.wait
import org.hamcrest.Description
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginAuthActivityTest {
    private val seedNum = 13

    @get:Rule
    val activity = ActivityScenarioRule(LoginAuthActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION")

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingresource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingresource)
    }

    @Test
    fun assertRegister() {
        // login -> register
        Intents.init()
        val fakeNumNext = Random(seedNum).nextInt()
        val fakeUser = StoryAuthRegisterBody("John Doe $fakeNumNext", "john${fakeNumNext}@doe.com", "johndoe${fakeNumNext}")
        onView(ViewMatchers.withId(R.id.btnStoryToRegister)).perform(ViewActions.click())
        Intents.intended(hasComponent(RegisterAuthActivity::class.java.name))

        // isi register
        onView(ViewMatchers.withId(R.id.edtNamaRegister)).perform(ViewActions.typeText(fakeUser.name))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.edtEmailRegister)).perform(ViewActions.typeText(fakeUser.email))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.edtPasswordRegister)).perform(ViewActions.typeText(fakeUser.password))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        // register -> login
        onView(ViewMatchers.withId(R.id.btnStoryRegisterProceed)).perform(ViewActions.click())
        Intents.intended(hasComponent(LoginAuthActivity::class.java.name))

        // isi login dengan user yang sama
        onView(ViewMatchers.withId(R.id.edtEmailLogin)).perform(ViewActions.typeText(fakeUser.email))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.edtPasswordLogin)).perform(ViewActions.typeText(fakeUser.password))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onView(ViewMatchers.withId(R.id.btnStoryLoginProceed)).perform(ViewActions.click())
        Intents.intended(hasComponent(MainActivity::class.java.name))
        Intents.release()
    }


    @Test
    fun assertLogin() {
        val freyaUser = StoryAuthRegisterBody("Freya Jayawardhana", "freya@freya.com", "freya123")
        Intents.init()
        onView(ViewMatchers.withId(R.id.edtEmailLogin)).perform(ViewActions.typeText(freyaUser.email))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.edtPasswordLogin)).perform(ViewActions.typeText(freyaUser.password))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onView(ViewMatchers.withId(R.id.btnStoryLoginProceed)).perform(ViewActions.click())
        Intents.intended(hasComponent(MainActivity::class.java.name))
        Intents.release()
    }
    @Test
    fun assertAddStory() {
        val freyaUser = StoryAuthRegisterBody("Freya Jayawardhana", "freya@freya.com", "freya123")
        val expectedString = "salam dari espresso dan uiautomator - #${Random.nextInt(1000,9999)}"
        Intents.init()
        onView(ViewMatchers.withId(R.id.edtEmailLogin)).perform(ViewActions.typeText(freyaUser.email))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.edtPasswordLogin)).perform(ViewActions.typeText(freyaUser.password))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onView(ViewMatchers.withId(R.id.btnStoryLoginProceed)).perform(ViewActions.click())
        Intents.intended(hasComponent(MainActivity::class.java.name))

        onView(ViewMatchers.withId(R.id.btnCreateStoryFAB)).perform(ViewActions.click())
        Intents.intended(hasComponent(AddStoryActivity::class.java.name))

        onView(ViewMatchers.withId(R.id.btnAddStoryImgCamera)).perform(ViewActions.click())
        // only work on AOSP camera2 (other than you must click manually :) )
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        executeUiAutomatorActions(device, CameraConstants.CAMERA_BUTTON_SHUTTER_ACTION_ID, CameraConstants.CAMERA_BUTTON_DONE_ACTION_ID)
        onView(ViewMatchers.withId(R.id.imgAddStory)).check(ViewAssertions.matches(ImageViewHasDrawableMatcher.hasDrawable()))

        onView(ViewMatchers.withId(R.id.imgAddStory))
            .check(ViewAssertions.matches(ImageViewHasDrawableMatcher.hasDrawable()))
        onView(ViewMatchers.withId(R.id.edtAddStoryDesc)).perform(ViewActions.typeText(expectedString))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onView(ViewMatchers.withId(R.id.btnUploadAddStory)).perform(ViewActions.click())
        Intents.intended(hasComponent(MainActivity::class.java.name))

        onView(ViewMatchers.withId(R.id.rcvStory))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(
                RecyclerViewActions.scrollTo<StoryListAdapter.ViewHolder>(ViewMatchers.hasDescendant(ViewMatchers.withText(expectedString))),
            )
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(expectedString))))

        Intents.release()
    }
    fun executeUiAutomatorActions(device: UiDevice, vararg ids: String, actionTimeOut: Long = 4000L) {
        for(id in ids) {
            val obj = device.findObject(UiSelector().resourceId(id))
            if(obj.waitForExists(actionTimeOut)) {
                obj.click()
            }
        }
    }

    object CameraConstants {
        const val CAMERA_BUTTON_SHUTTER_ACTION_ID = "com.android.camera2:id/shutter_button"
        const val CAMERA_BUTTON_DONE_ACTION_ID = "com.android.camera2:id/done_button"
    }
}

class ImageViewHasDrawableMatcher {
    companion object {
        fun hasDrawable() = object : BoundedDiagnosingMatcher<View, ImageView>(ImageView::class.java) {
            override fun describeMoreTo(description: Description?) {
                description?.appendText("has drawable")
            }

            override fun matchesSafely(
                item: ImageView?,
                mismatchDescription: Description?,
            ): Boolean {
                return item?.drawable != null
            }

        }
    }
}