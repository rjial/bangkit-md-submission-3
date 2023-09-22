package com.rjial.storybook.network.response

import com.google.gson.annotations.SerializedName

data class StoryAddResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
