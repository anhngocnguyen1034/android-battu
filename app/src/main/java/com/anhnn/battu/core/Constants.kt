package com.anhnn.battu.core

/**
 * Shared constants for the BatTu app (Anhnn ecosystem).
 */
object Constants {

    /**
     * FOR-BAZI backend base URL.
     *
     * - Real device on the same Wi-Fi: the host Mac's LAN IP (current value).
     * - Android emulator: use "http://10.0.2.2:8000/" instead (host loopback alias).
     *
     * Replace with the real Static Service URL for production builds.
     */
    const val BAZI_BASE_URL = "http://192.168.0.102:8000/"

    /** Address that receives user feedback (Settings → Feedback). */
    const val FEEDBACK_EMAIL = "nguyenanhcry@gmail.com"
}
