package br.com.angelica.comprainteligente.model

data class UserProgressState(
    val points: Int = 0,
    val level: Int = 1,
    val progress: Float = 0f
)