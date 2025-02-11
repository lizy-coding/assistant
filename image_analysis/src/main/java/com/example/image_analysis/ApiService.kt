import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class ImageRequest(val imageBase64: String)
data class ImageResponse(val result: String)

interface ApiService {
    @POST("https://your-ali-tongyi-api-url")
    suspend fun analyzeImage(@Body request: ImageRequest): ImageResponse
}

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://your-ali-tongyi-api-url")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
