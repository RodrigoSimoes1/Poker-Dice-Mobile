package com.example.myapplication.login.domain

import android.util.Patterns

/**
 * Data class representing user credentials as they are collected in the client application.
 * @param email The user's email address.
 * @param password The user's password.
 */
data class UserCredentials(
    val email: String,
    val password: String,
) {
    init {
        require(value = isValidCredentialsData(email, password)) { "Invalid user credentials: $this" }
    }
}

/**
 * Validates if the provided string is a valid email address. Validity in the client application is
 * determined simply by verifying if the string matches the expected pattern for email addresses.
 * In the overall solution, the complete validation is performed by the backend service.
 * @return True if the string is a valid email, false otherwise.
 */
fun String.isValidEmail(): Boolean = isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * Validates if the provided string is a valid password. Validity in the client application is
 * determined simply by verifying that the string is not blank. In the overall solution, the complete
 * validation is performed by the backend service.
 * @return True if the string is a valid password, false otherwise.
 */
fun String.isValidPassword(): Boolean = isNotBlank()

/**
 * Validates if a name is valid.
 * For simplicity, considers any non-blank string as valid.
 * @return True if valid, false otherwise.
 */
fun String.isValidName(): Boolean = isNotBlank()

/**
 * Validates if an invite code is valid.
 * In this client, considers any non-blank code as valid.
 * @return True if valid, false otherwise.
 */
fun String.isValidInviteCode(): Boolean = isNotBlank()

/**
 * Validates if the user credentials are valid. Validity in the client application is determined
 * by verifying both the email and password using their respective validation functions. In the
 * overall solution, the complete validation is performed by the backend service.
 * @param email The user's email address.
 * @param password The user's password.
 * @return True if both email and password are valid, false otherwise.
 */
fun isValidCredentialsData(
    email: String,
    password: String,
): Boolean = email.isValidEmail() && password.isValidPassword()

/**
 * Data class representing user registration data as collected in the client application.
 * @param name The user's display name.
 * @param email The user's email address.
 * @param password The user's password.
 * @param inviteCode The invite code required to register.
 */
data class RegisterData(
    val name: String,
    val email: String,
    val password: String,
    val inviteCode: String,
) {
    init {
        require(isValidRegisterData(name, email, password, inviteCode)) { "Invalid register data: $this" }
    }
}

/**
* Validates if registration data is valid.
* @return True if all fields are valid, false otherwise.
*/
fun isValidRegisterData(
    name: String,
    email: String,
    password: String,
    inviteCode: String,
): Boolean = name.isValidName() && email.isValidEmail() && password.isValidPassword() && inviteCode.isValidInviteCode()
