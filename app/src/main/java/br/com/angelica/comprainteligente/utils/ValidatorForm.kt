package br.com.angelica.comprainteligente.utils

object ValidatorForm {

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isFieldNotEmpty(field: String): Boolean {
        return field.isNotBlank()
    }

    fun isPasswordStrong(password: String): Boolean {
        return password.length >= 6
    }
}
