package com.rjial.storybook.util

object IsRunningTest {
    val isRunningText: Boolean by lazy {
        try {
            Class.forName("android.support.test.espresso.Espresso")
            true
        } catch (exc: ClassNotFoundException) {
            false
        }
    }
}