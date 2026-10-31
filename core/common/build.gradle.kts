plugins {
    id("moisesai.android.library")
}

android {
    namespace = "com.moisesai.challange.core.common"
}

dependencies {
    // api: DispatcherProvider exposes CoroutineDispatcher to every module that uses it.
    api(libs.kotlinx.coroutines.core)
}
