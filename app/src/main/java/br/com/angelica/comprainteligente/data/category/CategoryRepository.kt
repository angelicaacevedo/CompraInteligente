package br.com.angelica.comprainteligente.data.category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<String>>
    suspend fun addCategory(category: String): Result<Unit>
    suspend fun removeCategory(category: String): Result<Unit>
}