 class STSTokenResponse(
    val accessKeyId: String,
    val accessKeySecret: String,
    val securityToken: String,
    val expiration: Long  // 过期时间戳（秒）
)