package com.pharmatech.morocco.core.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pharmatech.morocco.BuildConfig
import com.pharmatech.morocco.core.database.PharmaTechDatabase
import com.pharmatech.morocco.core.network.ApiService
import com.pharmatech.morocco.core.network.AuthInterceptor
import com.pharmatech.morocco.core.utils.NetworkMonitor
import com.pharmatech.morocco.features.tracker.data.repository.TrackerStateRepositoryImpl
import com.pharmatech.morocco.features.tracker.domain.repository.TrackerStateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PharmaTechDatabase {
        return Room.databaseBuilder(
            context,
            PharmaTechDatabase::class.java,
            "pharmatech_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // DAO Providers
    @Provides
    @Singleton
    fun provideUserDao(database: PharmaTechDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideMedicationDao(database: PharmaTechDatabase) = database.medicationDao()

    @Provides
    @Singleton
    fun providePharmacyDao(database: PharmaTechDatabase) = database.pharmacyDao()

    @Provides
    @Singleton
    fun provideMedicationTrackerDao(database: PharmaTechDatabase) = database.trackerDao()

    @Provides
    @Singleton
    fun provideReminderDao(database: PharmaTechDatabase) = database.reminderDao()

    @Provides
    @Singleton
    fun provideFavoritePharmacyDao(database: PharmaTechDatabase) = database.favoriteDao()

    @Provides
    @Singleton
    fun provideMedicationHistoryDao(database: PharmaTechDatabase) = database.historyDao()

    @Provides
    @Singleton
    fun provideHealthInsightDao(database: PharmaTechDatabase) = database.insightDao()

    @Provides
    @Singleton
    fun provideTrackerStateRepository(
        impl: TrackerStateRepositoryImpl
    ): TrackerStateRepository = impl
}

