package com.rjial.storybook.network.response

data class StoryAuthRegisterBody(
    val name: String,
    val email: String,
    val password: String
)
