package br.com.alura.forum.model

object UsuarioTest {
    fun build() = Usuario(
        id = 1L,
        nome = "Stefany Mikaelle",
        email = "stefany@email.com",
        password = "1234"
    )
}