package com.anhnn.battu.data.repository

import android.content.Context
import android.os.Build
import com.anhnn.battu.data.datasource.BaziApiService
import com.anhnn.battu.data.models.FeedbackRequestDto
import com.anhnn.battu.domain.repository.FeedbackRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepositoryImpl @Inject constructor(
    private val api: BaziApiService,
    @ApplicationContext private val context: Context
) : FeedbackRepository {

    override suspend fun sendFeedback(rating: Int?, message: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                api.sendFeedback(
                    FeedbackRequestDto(
                        message = message,
                        rating = rating,
                        appVersion = appVersionName(),
                        device = Build.MODEL.orEmpty(),
                        osVersion = Build.VERSION.RELEASE.orEmpty(),
                        lang = Locale.getDefault().language
                    )
                )
                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                Result.failure(IOException("Server error ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun appVersionName(): String = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrNull().orEmpty()
}
