package br.com.alura.forum.mapper

import br.com.alura.forum.dto.TopicoView
import br.com.alura.forum.model.Topico
import org.springframework.stereotype.Component

@Component
class TopicoViewMapper : Mapper<Topico, TopicoView> {

    override fun map(form: Topico): TopicoView {
        return TopicoView(
            id = form.id,
            titulo = form.titulo,
            mensagem = form.mensagem,
            dataCriacao = form.dataCriacao,
            status = form.status,
            dataAlteracao = form.dataAlteracao,
        )
    }
}