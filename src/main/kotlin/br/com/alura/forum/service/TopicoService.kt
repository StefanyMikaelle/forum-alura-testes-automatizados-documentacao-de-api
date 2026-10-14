package br.com.alura.forum.service

import br.com.alura.forum.dto.AtualizacaoTopicoForm
import br.com.alura.forum.dto.NovoTopicoForm
import br.com.alura.forum.dto.TopicoPorCategoriaDto
import br.com.alura.forum.dto.TopicoView
import br.com.alura.forum.exception.NaoEncontradoException
import br.com.alura.forum.mapper.TopicoFormMapper
import br.com.alura.forum.mapper.TopicoViewMapper
import br.com.alura.forum.repository.TopicoRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.stream.Collectors

@Service
class TopicoService (
    private val repository: TopicoRepository,
    private var topicoFormMapper : TopicoFormMapper,
    private var topicoViewMapper : TopicoViewMapper,
    private val notFoundMessage: String = "Topico nao encontrado!"
) {

    fun cadastrar(form: NovoTopicoForm) : TopicoView {
        val topico = topicoFormMapper.map(form)
        repository.save(topico)
        return topicoViewMapper.map(topico)
    }

    fun atualizar (form: AtualizacaoTopicoForm) : TopicoView {
        val topico = repository.findById(form.id).orElseThrow { NaoEncontradoException(notFoundMessage) }
        topico.titulo = form.titulo
        topico.mensagem = form.mensagem
        topico.dataAlteracao = LocalDate.now()
        return topicoViewMapper.map(topico)
    }

    fun listar(
        nomeCurso : String?,
        paginacao: Pageable
        ): Page<TopicoView> {
        val topicos = if(nomeCurso == null){
            repository.findAll(paginacao)
        } else {
            repository.findByCursoNome(nomeCurso , paginacao)
        }
        return topicos.map{ t ->
            topicoViewMapper.map(t)
        }
    }

    fun buscarPorId(id: Long) : TopicoView {
        val topico = repository.findById(id).orElseThrow { NaoEncontradoException(notFoundMessage) }
        return topicoViewMapper.map(topico)
    }

    fun deletar(id: Long){
        repository.deleteById(id)
    }
    fun relatorio(): List<TopicoPorCategoriaDto>{
        return repository.relatorio()
    }
}