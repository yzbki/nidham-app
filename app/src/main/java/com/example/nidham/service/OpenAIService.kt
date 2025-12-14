package com.example.nidham.service

import com.example.nidham.data.ListData
import com.example.nidham.data.ListItem
import com.google.android.gms.tasks.Tasks
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Retrofit API definitions
interface FirebaseOpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("/") // Firebase function expects the body at root
    suspend fun getChatCompletion(
        @Body request: ChatRequest
    ): ChatResponse
}

// Data classes for OpenAI Chat API
data class Message(val role: String, val content: String)
data class ChatRequest(val model: String, val messages: List<Message>, val temperature: Double = 0.7)
data class Choice(val index: Int, val message: Message, @SerializedName("finish_reason") val finishReason: String?)
data class ChatResponse(val id: String, val choices: List<Choice>)

// Data classes to map your list JSON format
data class ListDataJson(val title: String, val tasks: List<TaskItemJson>, val checkedStates: List<Boolean>)
data class TaskItemJson(val text: String)

// Service singleton
object OpenAIService {

    // Replace with your Firebase function URL
    private const val BASE_URL = "https://chat-uyw75ruqmq-uc.a.run.app/"

    // Interceptor to add App Check token
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val original: Request = chain.request()
            val builder = original.newBuilder()

            // Fetch App Check token synchronously (use Tasks.await)
            val appCheckToken = Tasks.await(FirebaseAppCheck.getInstance().getAppCheckToken(false)).token
            builder.header("X-Firebase-AppCheck", appCheckToken)

            chain.proceed(builder.build())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: FirebaseOpenAIApi = retrofit.create(FirebaseOpenAIApi::class.java)
    private val gson = Gson()

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
                                "Each task must be at most ${ListData.MAX_TASK_LENGTH} characters. " +
                                "The list must have at most ${ListData.MAX_TASKS} tasks. " +
                                "Always generate a list within these limits."
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

            val listData = ListData.newListData()
            listData.title.value = listDataJson.title.take(ListData.MAX_TITLE_LENGTH)

            listData.items.clear()
            listData.items.addAll(listDataJson.tasks.map {
                ListItem.TaskItem().apply { textState.value = it.text }
            })

            listData.checkedStates.clear()
            val checks = listDataJson.checkedStates.take(listData.items.size)
            listData.checkedStates.addAll(checks)
            while (listData.checkedStates.size < listData.items.size) {
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