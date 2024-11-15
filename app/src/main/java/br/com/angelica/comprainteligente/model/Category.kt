package br.com.angelica.comprainteligente.model

data class Category(
    val id: String,
    val name: String
)

object CategoryRepository {
    val categories = listOf(
        Category(id = "1", name = "Bebidas"),
        Category(id = "2", name = "Alimentos"),
        Category(id = "3", name = "Higiene Pessoal"),
        Category(id = "4", name = "Limpeza"),
        Category(id = "5", name = "Frios e Laticínios"),
        Category(id = "6", name = "Carnes e Aves"),
        Category(id = "7", name = "Peixes e Frutos do Mar"),
        Category(id = "8", name = "Frutas"),
        Category(id = "9", name = "Legumes e Verduras"),
        Category(id = "10", name = "Padaria e Confeitaria"),
        Category(id = "11", name = "Cereais e Grãos"),
        Category(id = "12", name = "Massas e Molhos"),
        Category(id = "13", name = "Óleos e Condimentos"),
        Category(id = "14", name = "Congelados"),
        Category(id = "15", name = "Doces e Sobremesas"),
        Category(id = "16", name = "Biscoitos e Snacks"),
        Category(id = "17", name = "Enlatados e Conservas"),
        Category(id = "18", name = "Papelaria"),
        Category(id = "19", name = "Pet Shop"),
        Category(id = "20", name = "Produtos Naturais e Orgânicos"),
        Category(id = "21", name = "Bebidas Alcoólicas"),
        Category(id = "22", name = "Bebidas Não Alcoólicas"),
        Category(id = "23", name = "Café e Chá"),
        Category(id = "24", name = "Açúcar e Doces"),
        Category(id = "25", name = "Suplementos e Vitaminas")
    )
}
