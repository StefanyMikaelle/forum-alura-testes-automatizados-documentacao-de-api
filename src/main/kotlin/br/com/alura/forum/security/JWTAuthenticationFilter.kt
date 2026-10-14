package br.com.alura.forum.security

import br.com.alura.forum.config.JWTUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JWTAuthenticationFilter (private val jwtUtil: JWTUtil) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        println(">>> 1. Header: $header")

        val jwt = getTokenDetail(header)
        println(">>> 2. JWT extraído: ${jwt?.take(20)}")

        if (jwt != null) {
            try {
                val auth = jwtUtil.getAuthentication(jwt)
                println(">>> 3. autenticado=${auth.isAuthenticated}, authorities=${auth.authorities}")
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: Exception) {
                println(">>> 3. ERRO: ${e.javaClass.simpleName} - ${e.message}")
            }
        }
        filterChain.doFilter(request, response)
    }

//    override fun doFilterInternal(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        filterChain: FilterChain
//    ) {
//        val token = request.getHeader("Authorization")
//        val jwt = getTokenDetail(token)
//        if (jwtUtil.isValid(jwt)) {
//            val authentication = jwtUtil.getAuthentication(jwt)
//            SecurityContextHolder.getContext().authentication = authentication
//        }
//        filterChain.doFilter(request, response)
//    }
//
    private fun getTokenDetail(token: String?): String? {
        return if (token != null && token.startsWith("Bearer ")) {
            token.substring(7)
        } else {
            null
        }
    }

}