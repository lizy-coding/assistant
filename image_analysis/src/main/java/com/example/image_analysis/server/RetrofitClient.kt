//import com.example.image_analysis.BuildConfig
//import com.google.gson.annotations.SerializedName
//import okhttp3.OkHttpClient
//import okhttp3.Response
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import java.util.concurrent.TimeUnit
//import javax.net.ssl.SSLContext
//import javax.net.ssl.X509TrustManager
//
//object RetrofitClient {
//    private const val PROD_BASE_URL = "https://api.yourdomain.com/"
//    private const val DEBUG_BASE_URL = "https://dev-api.yourdomain.com/"
//
//    // 安全忽略SSL证书（仅限测试环境）
//    private fun unsafeOkHttpClient(): OkHttpClient {
//        val trustAllCerts = arrayOf(object : X509TrustManager {
//            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
//            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
//            override fun getAcceptedIssuers() = arrayOf<java.security.cert.X509Certificate>()
//        })
//
//        val sslContext = SSLContext.getInstance("SSL")
//        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
//
//        return OkHttpClient.Builder()
//            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
//            .hostnameVerifier { _, _ -> true }
//            .build()
//    }
//
//    private val client: OkHttpClient by lazy {
//        OkHttpClient.Builder().apply {
//            connectTimeout(15, TimeUnit.SECONDS)
//            readTimeout(30, TimeUnit.SECONDS)
//            writeTimeout(30, TimeUnit.SECONDS)
//
//            // 调试模式增加日志拦截器
//            if (BuildConfig.DEBUG) {
//                addInterceptor(HttpLoggingInterceptor().apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                })
//            }
//
//            // 生产环境证书验证
//            if (!BuildConfig.DEBUG) {
//                sslSocketFactory(sslContext.socketFactory, X509TrustManagerWrapper)
//            }
//        }.build()
//    }
//
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(if (BuildConfig.DEBUG) DEBUG_BASE_URL else PROD_BASE_URL)
//            .client(if (BuildConfig.DEBUG) unsafeOkHttpClient() else client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
//            .build()
//    }
//
//    val stsService: STSService by lazy {
//        retrofit.create(STSService::class.java)
//    }
//}
//
//// 修正后的STS服务接口
//interface STSService {
//    @GET("/api/v1/sts/ocr-token")
//    suspend fun getOcrStsToken(): Response<STSTokenResponse>
//}
//
//// 增强的STS响应体
//data class STSTokenResponse(
//    val code: Int,
//    val data: STSCredentials?
//)
//
//data class STSCredentials(
//    @SerializedName("AccessKeyId") val accessKeyId: String,
//    @SerializedName("AccessKeySecret") val accessKeySecret: String,
//    @SerializedName("SecurityToken") val securityToken: String,
//    @SerializedName("Expiration") val expiration: String
//)

//class STSTokenResponse(
//    val accessKeyId: String,
//    val accessKeySecret: String,
//    val securityToken: String,
//    val expiration: Long  // 过期时间戳（秒）
//)