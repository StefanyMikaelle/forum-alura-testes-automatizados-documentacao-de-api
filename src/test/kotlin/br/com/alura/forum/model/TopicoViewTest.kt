package br.com.alura.forum.model

import br.com.alura.forum.dto.TopicoView
import br.com.alura.forum.enums.StatusTopico
import java.time.LocalDate
import java.time.LocalDateTime

object TopicoViewTest {
    fun build() = TopicoView(
        id = 1,
        titulo = "Kotlin Basico",
        mensagem = "Aprendendo kotlin basico",
        status = StatusTopico.SOLUCIONADO,
        dataCriacao = LocalDateTime.of(2021, 1, 1, 1, 0),
        dataAlteracao = LocalDate.now()
    )
}