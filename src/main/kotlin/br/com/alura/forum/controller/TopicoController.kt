package br.com.alura.forum.controller

import br.com.alura.forum.dto.AtualizacaoTopicoForm
import br.com.alura.forum.dto.NovoTopicoForm
import br.com.alura.forum.dto.TopicoPorCategoriaDto
import br.com.alura.forum.dto.TopicoView
import br.com.alura.forum.service.TopicoService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@SecurityRequirement(name = "bearer-token")
@RequestMapping("/topicos")
class TopicoController (private val service: TopicoService){

    @GetMapping
    @Cacheable("topicos")
    fun listar(
        @RequestParam(required = false) nomeCurso: String?,
        @PageableDefault(size=5, sort = ["dataCriacao"], direction = Sort.Direction.DESC) paginacao: Pageable,
    ): Page<TopicoView>{
        return service.listar(nomeCurso , paginacao)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): TopicoView{
        return service.buscarPorId(id)
    }

    @PostMapping
    @CacheEvict(value = ["topicos"], allEntries = true) //usado para invalidar o cache ( pode ser um ou mais de uma vez )
    @Transactional // depois que adiciona o banco de dados , tem que adicionar isso em todos os
                    // métodos de escrita
    fun cadastrar (
        @RequestBody @Valid form: NovoTopicoForm,
        uriBuilder : UriComponentsBuilder
    ) : ResponseEntity<TopicoView>{
        val topicoView = service.cadastrar(form)
        val uri = uriBuilder.path("/topicos/${topicoView.id}").build().toUri()
        return ResponseEntity.created(uri).body(topicoView)
    }

    @PutMapping
    @Transactional // depois que adiciona o banco de dados , tem que adicionar isso em todos os
    // métodos de escrita
    fun atualizar (@RequestBody @Valid form: AtualizacaoTopicoForm) : ResponseEntity<TopicoView> {
        val topicoView = service.atualizar(form)
        return ResponseEntity.ok(topicoView)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = ["topicos"], allEntries = true)
    @Transactional // depois que adiciona o banco de dados , tem que adicionar isso em todos os
    // métodos de escrita
    fun deletar (@PathVariable id: Long){
        service.deletar(id)
    }

    @GetMapping("/relatorio")
    fun relatorio() : List<TopicoPorCategoriaDto> {
        return service.relatorio()
    }

}