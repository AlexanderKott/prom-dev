package ru.netology.nmedia.auth

import android.content.Context
import kotlinx.coroutines.flow.*

/**
 * Здесь синглетон класс который работает с шаред преференсес
 */
class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _authStateFlow: MutableStateFlow<AuthState>


    //если токена и id нет - то почистим преференсы на старте
    init {
        val id = prefs.getLong(idKey, 0)
        val token = prefs.getString(tokenKey, null)

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
               clear()
               apply()
           }
        } else {
            //иначе проиницаилизируем этим значениями поток
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
    }


    /**
     * StateFlow это такая лайвдата от мира котлин: это значит он будет изменяться даже если
     * на него никто не подписан.
     *
     *  Чем она меняется по ходу работы приложения?
     *      загрузка из щаред преференсес
     *      сам юзер (входом/выходом)
     *
     * Его юзают:
     * + AuthViewModel   (для показа менюшки логина)
     * + PostViewModel   (для того чтобы пометить какие посты свои)
     * + okhttp PostService
     */
    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow() //Read only




    @Synchronized
    fun setAuth(id: Long, token: String?) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }

    companion object {
        @Volatile
        private var instance: AppAuth? = null //синглетон

        //либо верни экземпляр либо ошибку
        fun getInstance(): AppAuth = synchronized(this) {
            instance ?: throw IllegalStateException(
                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        //верни инстанс если он есть, если нет то получи его из приватного метода и верни
        fun initApp(context: Context): AppAuth = instance ?: synchronized(this) {
            instance ?: buildAuth(context).also { instance = it }
        }

        private fun buildAuth(context: Context): AppAuth = AppAuth(context)
    }
}

//дата класс который хранит данные аутентификации
data class AuthState(val id: Long = 0, val token: String? = null)