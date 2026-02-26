package com.example.myapplication.login.domain

typealias LoginUseCase = suspend (
    credentials: UserCredentials,
    authService: AuthService,
    authInfoRepo: AuthInfoRepo,
) -> AuthInfo

/**
 * Performs the implementation of the login use case.
 * @param credentials The user's credentials.
 * @param authService The service to be used for login.
 * @param authInfoRepo The repository to store the authentication information.
 * @return The authentication information upon successful login.
 */
suspend fun performLogin(
    credentials: UserCredentials,
    authService: AuthService,
    authInfoRepo: AuthInfoRepo,
): AuthInfo {
    val tokenResponse = authService.login(credentials.email, credentials.password).getOrThrow()
    val token = tokenResponse.token
    val userDetails = authService.fetchMe(token).getOrThrow()
    val authInfo = AuthInfo(userEmail = credentials.email, authToken = token, userId = userDetails.id)
    authInfoRepo.saveAuthInfo(authInfo)
    return authInfo
}

/**
 * Representa o caso de uso do registo.
 */
typealias RegisterUseCase = suspend (
    data: RegisterData,
    authService: AuthService,
    authInfoRepo: AuthInfoRepo,
) -> AuthInfo

/**
 * Implementação do caso de uso do registo.
 */
suspend fun performRegister(
    data: RegisterData,
    authService: AuthService,
    authInfoRepo: AuthInfoRepo,
): AuthInfo {
    authService
        .register(
            name = data.name,
            email = data.email,
            password = data.password,
            inviteCode = data.inviteCode,
        ).getOrThrow()

    val tokenResponse = authService.login(data.email, data.password).getOrThrow()
    val token = tokenResponse.token
    val userDetails = authService.fetchMe(token).getOrThrow()
    val authInfo =
        AuthInfo(
            userEmail = data.email,
            authToken = tokenResponse.token,
            userId = userDetails.id,
        )
    authInfoRepo.saveAuthInfo(authInfo)
    return authInfo
}

/**
 * Representa o caso de uso do logout.
 */
typealias LogoutUseCase = suspend (
    authService: AuthService,
    authInfoRepo: AuthInfoRepo,
) -> Unit

/**
 * Implementação do caso de uso do logout.
 */
suspend fun performLogout(
    authService: AuthService,
    authInfoRepo: AuthInfoRepo,
) {
    val token = authInfoRepo.getAuthInfo()?.authToken

    authService.logout(token!!)
    authInfoRepo.clearAuthInfo()
}
