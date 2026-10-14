package br.com.alura.forum.controller

import br.com.alura.forum.config.JWTUtil
import br.com.alura.forum.model.Role
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import kotlin.test.Test

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ,
    properties = ["jwt.secret=9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a"]
)
class TopicoControllerTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var jwtUtil: JWTUtil

    private var token: String? = null

    companion object {
        private const val RECURSO = "/topicos"
        private const val RECURSO_ID = "$RECURSO/%s"
    }

    @BeforeEach
    fun setUp() {
        token = gerarToken()

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    @Test
    fun `deve retornar 400 quando chamar topicos sem token`() {
        mockMvc.get(RECURSO).andExpect {status { is4xxClientError()}}
    }

    @Test
    fun `deve retornar 200 quando chamar topicos com token`() {
        mockMvc.get(RECURSO){
            headers { token?.let { setBearerAuth(it) } }
        }.andDo { print() }
            .andExpect {  status { is2xxSuccessful() } }
    }
    @Test
    fun `deve retornar codigo 200 quando chamar topico id com token`(){
        mockMvc.get(RECURSO_ID.format("1")) {
            headers { token?.let { setBearerAuth(it) } }
        }.andExpect { status { is2xxSuccessful() } }
    }

    private fun gerarToken() : String {
        val authorities = mutableListOf(Role(1,"LEITURA_ESCRITA"))
        return jwtUtil.generateToken("stefany@email.com", authorities)
    }
}