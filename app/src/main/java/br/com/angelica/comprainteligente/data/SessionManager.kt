package br.com.angelica.comprainteligente.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userId: String?
        get() = prefs.getString("user_id", null)
        set(value) {
            prefs.edit().putString("user_id", value).apply()
        }

    fun logout() {
        prefs.edit().remove("user_id").apply()
    }
}
