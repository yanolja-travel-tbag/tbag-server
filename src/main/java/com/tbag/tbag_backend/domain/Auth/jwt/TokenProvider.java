package com.tbag.tbag_backend.domain.Auth.jwt;

import com.tbag.tbag_backend.domain.Auth.dto.TokenResponse;
import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import com.tbag.tbag_backend.util.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
public class TokenProvider implements
        InitializingBean {

    public static final String AUTHORITIES = "auth";
    private final String secret;
    private SecretKey key;

    @Value("${jwt.secret}")
    public String stringSecret;
    private final RedisTemplate<String, String> redisTemplate;
    private final long accessExpired;
    private final long refreshExpired;
    private final UserRepository userRepository;
    static final String BEARER_PREFIX = "Bearer ";

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            RedisTemplate<String, String> redisTemplate, @Value("${jwt.access_expired-time}") long accessExpired,
            @Value("${jwt.refresh_expired-time}") long refreshExpired,
            UserRepository userRepository) {
        this.secret = secret;
        this.redisTemplate = redisTemplate;
        this.accessExpired = accessExpired;
        this.refreshExpired = refreshExpired;
        this.userRepository = userRepository;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] decoded = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(decoded);
    }

    public TokenResponse createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        User user = userRepository.findBySocialIdAndIsActivatedIsTrue(authentication.getName()).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found with social id: " + authentication.getName()));

        return createTokenForUser(user, authorities);
    }

    public TokenResponse createRefreshToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        User user = userRepository.findOneByIdAndIsActivatedIsTrue(Integer.valueOf(authentication.getName())).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found with id: " + authentication.getName()));

        return createTokenForUser(user, authorities);
    }

    private TokenResponse createTokenForUser(User user, String authorities) {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        Date accessExpiration = Date.from(issuedAt.plus(accessExpired, ChronoUnit.SECONDS));
        Date refreshExpiration = Date.from(issuedAt.plus(refreshExpired, ChronoUnit.SECONDS));

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("TBag")
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(accessExpiration)
                .claim("userId", user.getId())
                .signWith(key, SignatureAlgorithm.HS512)
                .claim("authorities", authorities)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("TBag")
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(refreshExpiration)
                .claim("userId", user.getId())
                .signWith(key, SignatureAlgorithm.HS512)
                .claim("authorities", authorities)
                .compact();

        updateUserAndStoreRefreshToken(user, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    private void updateUserAndStoreRefreshToken(User user, String refreshToken) {
        user.updateLastAccessed(LocalDateTime.now());
        userRepository.save(user);
        redisTemplate.opsForValue().set(user.getId().toString(), refreshToken, refreshExpired, TimeUnit.SECONDS);
    }

    public Authentication resolveFrom(String token, HttpServletRequest request) {
        JwtParser jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build();
        Claims claims = jwtParser
                .parseClaimsJws(token)
                .getBody();

        Integer userId = claims.get("userId", Integer.class);

        User user = userRepository.findOneByIdAndIsActivatedIsTrue(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN, "User not found"));

        Collection<SimpleGrantedAuthority> authorities = Stream.of(
                        String.valueOf(claims.get(AUTHORITIES)).split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails userDetails = UserPrincipal.create(user, authorities);

        request.setAttribute("id", userId);

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean validate(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "invalid token " + e);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED, "expired token " + e);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "token not supported " + e);
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        throw new CustomException(ErrorCode.INVALID_TOKEN, "token is invalid during resolving the token, token is " + token);
    }

}
