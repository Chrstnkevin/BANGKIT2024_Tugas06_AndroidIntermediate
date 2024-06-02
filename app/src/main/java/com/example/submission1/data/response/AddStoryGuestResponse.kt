package com.example.submission1.data.response

import com.google.gson.annotations.SerializedName

data class AddStoryGuestResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
