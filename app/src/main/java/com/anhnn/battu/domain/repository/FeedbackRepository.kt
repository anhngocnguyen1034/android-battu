package com.anhnn.battu.domain.repository

/** Sends user feedback (rating + message) to the backend → Discord. */
interface FeedbackRepository {

    /**
     * @param rating 1–5 stars, or null when the user skipped rating.
     * @param message free-form feedback text (non-blank).
     */
    suspend fun sendFeedback(rating: Int?, message: String): Result<Unit>
}
