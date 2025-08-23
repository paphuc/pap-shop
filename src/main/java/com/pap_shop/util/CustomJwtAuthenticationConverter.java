package com.pap_shop.util;
import com.pap_shop.repository.InvalidatedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        if (invalidatedTokenRepository.existsByJti(jwt.getId())) {
            throw new JwtException("Token has been logged out");
        }


        Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        if (jwt.containsClaim("scope")) {
            String scope = jwt.getClaimAsString("scope");
            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("SCOPE_" + scope);
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}