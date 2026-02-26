package com.example.myapplication.error

class ApiException(
    error: ApiErrorResponse,
) : Exception(error.title)
