plugins {
    id("moisesai.android.library")
}

android {
    namespace = "com.moisesai.challange.core.datacommon"
}

dependencies {
    // api: every function here returns or emits AppError, which the datasource modules expose.
    api(project(":core:common"))
}
