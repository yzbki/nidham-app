package com.example.nidham

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Retrofit API definitions

interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Body request: ChatRequest
    ): ChatResponse
}

// Data classes for OpenAI Chat API

data class Message(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Choice(
    val index: Int,
    val message: Message,
    @SerializedName("finish_reason")
    val finishReason: String?
)

data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

// Data classes to map your list JSON format

/*
{
  "title": "My List Title",
  "tasks": [
    { "id": "uuid1", "text": "Task 1" },
    { "id": "uuid2", "text": "Task 2" }
  ],
  "checkedStates": [true, false]
}
*/

data class ListDataJson(
    val title: String,
    val tasks: List<TaskItemJson>,
    val checkedStates: List<Boolean>
)

data class TaskItemJson(
    val id: String,
    val text: String
)

// Service singleton

object OpenAIService {

    private const val BASE_URL = "https://api.openai.com/"

    private val apiKey = BuildConfig.OPENAI_API_KEY

    private val authInterceptor = Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        chain.proceed(newRequest)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: OpenAIApi = retrofit.create(OpenAIApi::class.java)

    private val gson = Gson()

    /**
     * Sends a prompt to OpenAI API, parses the JSON response into ListData, or returns null on failure.
     */
    suspend fun generateListDataFromPrompt(
        prompt: String,
        onRawResponse: ((String) -> Unit)? = null
    ): ListData? = withContext(Dispatchers.IO) {
        try {
            val request = ChatRequest(
                model = "gpt-4o-mini",
                messages = listOf(
                    Message(
                        "system",
                        "You are an assistant that outputs only raw JSON (no markdown or code blocks). Format: { title: String, tasks: [{ id: String, text: String }], checkedStates: [Boolean] }"
                    ),
                    Message("user", prompt)
                ),
                temperature = 0.7
            )

            val response = api.getChatCompletion(request)
            val rawText = response.choices.firstOrNull()?.message?.content ?: return@withContext null

            // Optional callback with raw response
            onRawResponse?.invoke(rawText)

            // Clean markdown formatting (```json ... ```)
            val cleanedJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            // Parse cleaned JSON into ListDataJson
            val listDataJson = gson.fromJson(cleanedJson, ListDataJson::class.java)

            val listData = ListData()
            listData.title.value = listDataJson.title

            listData.tasks.clear()
            listData.tasks.addAll(listDataJson.tasks.map {
                TaskItem(id = it.id).apply {
                    textState.value = it.text
                }
            })

            listData.checkedStates.clear()
            listData.checkedStates.addAll(listDataJson.checkedStates)

            return@withContext listData
        } catch (e: Exception) {
            onRawResponse?.invoke("ERROR: ${e.message ?: "Unknown error"}")
            e.printStackTrace()
            return@withContext null
        }
    }
}
