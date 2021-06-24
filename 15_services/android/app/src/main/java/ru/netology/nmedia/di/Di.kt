package ru.netology.nmedia.di

import android.content.Context
import androidx.work.WorkManager
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject
import javax.inject.Singleton



@Module
@InstallIn(ViewModelComponent::class)
internal object ModuleForViewModel{

    @Provides
    fun getWorkManager(@ApplicationContext context: Context): WorkManager =
     WorkManager.getInstance(context)

}

@Module
@InstallIn(SingletonComponent::class)
internal object ModuleForSingleton {

    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

    @Singleton
    @Provides
    fun getGoogleApiAvailability() = GoogleApiAvailability.getInstance()

    @Singleton
    @Provides
    fun getFirebaseInstallations() = FirebaseInstallations.getInstance()

    @Singleton
    @Provides
    fun getFirebaseMessaging() =  FirebaseMessaging.getInstance()

    @Singleton
    @Provides
    fun getPostRepository(db: AppDb, api: ApiService): PostRepository =
        PostRepositoryImpl(db, api)

    @Provides
    fun getAppDb(@ApplicationContext context: Context) = AppDb.getInstance(context = context)


    @Provides
    fun getAppAuth(@ApplicationContext context : Context): AppAuth {
        AppAuth.initApp(context)
       return AppAuth.getInstance1()
    }


    @Provides
    fun getApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    @Provides
    fun getRetrofit(okhttp: OkHttpClient) = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okhttp)
        .build()



    @Provides
    fun getService(appAuth : AppAuth)  = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            appAuth.authStateFlow.value.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .build()


    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }






}
