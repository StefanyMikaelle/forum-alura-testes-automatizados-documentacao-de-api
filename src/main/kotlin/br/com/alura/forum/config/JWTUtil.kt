package br.com.alura.forum.config

import br.com.alura.forum.service.UsuarioService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JWTUtil (
    private val usuarioService: UsuarioService
) {

    private val expiration: Long = 60000

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(username: String, authorities: MutableCollection<out GrantedAuthority>): String {
        return Jwts.builder()
            .subject(username)
            .claim("roles", authorities)
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key , Jwts.SIG.HS512)
            .compact()
    }


    fun getAuthentication(jwt: String?): Authentication {
        val username = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(jwt)
            .payload
            .subject
        val user = usuarioService.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(username, null, user.authorities )
    }
}
