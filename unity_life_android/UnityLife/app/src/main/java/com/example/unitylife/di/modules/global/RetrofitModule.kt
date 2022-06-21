package com.example.unitylife.di.modules.global

import androidx.annotation.NonNull
import com.example.unitylife.BuildConfig
import com.example.unitylife.BuildConfig.DEBUG
import com.example.unitylife.network.AuthInterceptor
import com.example.unitylife.network.LoggingInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import utils.SharedPreferencesStorage
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    @NonNull
    fun provideRetrofit(
        httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @NonNull
    fun provideHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        loggingInterceptor: LoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @NonNull
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.apply {
            level =
                if (DEBUG) (HttpLoggingInterceptor.Level.BODY) else HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    @Provides
    @Singleton
    @NonNull
    fun provideLoggingInterceptor(): LoggingInterceptor = LoggingInterceptor()

    @Provides
    @Singleton
    @NonNull
    fun provideAuthInterceptor(
        storage: SharedPreferencesStorage,
    ): AuthInterceptor {
        return AuthInterceptor(storage)
    }
}