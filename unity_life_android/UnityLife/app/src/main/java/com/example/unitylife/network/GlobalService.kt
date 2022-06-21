package com.example.unitylife.network

import com.example.unitylife.App
import com.example.unitylife.data.models.CategoryModel
import com.example.unitylife.network.services.AppealService
import com.example.unitylife.network.services.AuthService
import com.example.unitylife.network.services.CategoriesService
import com.example.unitylife.network.services.FilesService
import com.example.unitylife.ui.fragments.CategoryBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object GlobalService {
    //private lateinit var categoriesService: CategoriesService
    //private lateinit var filesService: FilesService
    private lateinit var authService: AuthService

    fun init(app: App) {
        //categoriesService = app.getAppComponent().getCategoriesService()
        //filesService = app.getAppComponent().getFilesService()
        authService = app.getAppComponent().getAuthService()
    }

    /*suspend fun uploadImage(filePath: String): String? {
        kotlin.runCatching {
            val file = File(filePath)
            val part = MultipartBody.Part.createFormData(
                "image",
                "na.jpg",
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            val response = filesService.loadImage(part)
            if (response.isSuccessful) {
                return response.body()
            }
        }
        return null
    }

    suspend fun getAllCategories(): Result<MutableList<CategoryModel>> {
        val result = kotlin.runCatching {
            val response = categoriesService.getListOfCategories()
            val body = response.body() ?: mutableListOf()
            return if (response.isSuccessful && body.isNotEmpty()) {
                Result.Success(body)
            } else {
                Result.Error(response.code())
            }
        }
        return Result.Error(502, result.exceptionOrNull()?.message)
    }

    suspend fun setCategories(list: CategoryBody): Result<Unit> {
        val response = categoriesService.setCategoryList(list)
        return if (response.isSuccessful) {
            Result.Success(Unit)
        } else {
            Result.Error(response.code())
        }
    }*/
}