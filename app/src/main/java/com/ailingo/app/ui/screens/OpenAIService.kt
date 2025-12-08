package com.ailingo.app.ui.screens

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ChatMessage(val role: String, val content: String)

data class ChatRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>
)

data class ChatResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val message: ChatMessage
    )
}

interface OpenAIService {
    @Headers(
        "Content-Type: application/json",
        //TODO: Replace * with API KEY
        "Authorization: Bearer *"
    )
    @POST("chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}

object OpenAIClient {
    val api: OpenAIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)
    }
}
