package NoobSave._L.garcia.NoobSave.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Fournisseur de tokens JWT pour gérer la génération, la validation
 * et l'extraction d'informations des JSON Web Tokens (JWT).
 *
 * Cette classe permet :
 * <ul>
 *   <li>De générer des tokens pour les utilisateurs authentifiés.</li>
 *   <li>De valider les tokens pour vérifier leur authenticité et leur validité.</li>
 *   <li>D'extraire les informations contenues dans les tokens, telles que le nom d'utilisateur.</li>
 * </ul>
 *
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Component} : Enregistre cette classe comme composant Spring.</li>
 * </ul>
 */
@Component
public class JwtProvider {

    /**
     * Clé secrète utilisée pour signer et vérifier les tokens JWT.
     * <p>
     * Cette valeur est injectée depuis les propriétés de configuration
     * via la clé {@code jwt.secret}.
     * </p>
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Durée de validité d'un token JWT en millisecondes.
     * <p>
     * Par défaut, 1 heure (3600000 ms). Vous pouvez ajuster cette durée
     * selon les besoins de votre application.
     * </p>
     */
    private long expirationMs = 3600000;

    /**
     * Génère un token JWT pour un utilisateur donné.
     *
     * @param username Le nom d'utilisateur pour lequel le token est généré.
     * @return Une chaîne de caractères représentant le token JWT.
     *
     * <strong>Exemple :</strong>
     * <pre>{@code
     * String token = jwtProvider.generateToken("username123");
     * }</pre>
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur contenu dans un token JWT.
     *
     * @param token Le token JWT dont le nom d'utilisateur doit être extrait.
     * @return Le nom d'utilisateur contenu dans le token.
     *
     * <strong>Exemple :</strong>
     * <pre>{@code
     * String username = jwtProvider.getUsernameFromToken(token);
     * }</pre>
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valide un token JWT en vérifiant sa signature et sa date d'expiration.
     *
     * @param token Le token JWT à valider.
     * @return {@code true} si le token est valide, {@code false} s'il est expiré ou invalide.
     *
     * <strong>Exemple :</strong>
     * <pre>{@code
     * boolean isValid = jwtProvider.validateToken(token);
     * }</pre>
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false; // Le token est expiré ou invalide
        }
    }
}
