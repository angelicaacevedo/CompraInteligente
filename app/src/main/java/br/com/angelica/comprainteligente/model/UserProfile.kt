package br.com.angelica.comprainteligente.model

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val preferredSupermarket: String = "",
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val interactionHistory: List<String> = emptyList(),
    val favoriteSupermarkets: List<String> = emptyList(),
    val favoriteProducts: List<String> = emptyList(),
    val contributionLevel: Int = 0,
    val badges: List<String> = emptyList()
)

data class NotificationPreferences(
    val promotions: Boolean = false,
    val priceAlerts: Boolean = false
)
