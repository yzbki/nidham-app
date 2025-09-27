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
                        "You are an assistant that outputs only raw JSON (no markdown or code blocks). " +
                                "Format: { title: String, tasks: [{ id: String, text: String }], checkedStates: [Boolean] }. " +
                                "The title must be at most ${ListData.MAX_TITLE_LENGTH} characters. " +
                                "Do not exceed this limit, and do not truncate it yourself." +
                                "Always generate a title within this limit."
                    ),
                    Message("user", prompt)
                ),
                temperature = 0.7
            )

            val response = api.getChatCompletion(request)
            val rawText = response.choices.firstOrNull()?.message?.content ?: return@withContext null

            onRawResponse?.invoke(rawText)

            val cleanedJson = rawText.replace("```json", "").replace("```", "").trim()

            val listDataJson = gson.fromJson(cleanedJson, ListDataJson::class.java)

            // Create proper ListData object
            val listData = ListData.newListData()
            listData.title.value = listDataJson.title.take(ListData.MAX_TITLE_LENGTH)

            // Map tasks to TaskItem objects
            listData.tasks.clear()
            listData.tasks.addAll(listDataJson.tasks.map { TaskItem().apply { textState.value = it.text } })

            // Map checkedStates and fill remaining with false
            listData.checkedStates.clear()
            val checks = listDataJson.checkedStates.take(listData.tasks.size)
            listData.checkedStates.addAll(checks)
            while (listData.checkedStates.size < listData.tasks.size) {
                listData.checkedStates.add(false)
            }

            return@withContext listData
        } catch (e: Exception) {
            onRawResponse?.invoke("ERROR: ${e.message ?: "Unknown error"}")
            e.printStackTrace()
            return@withContext null
        }
    }
}
