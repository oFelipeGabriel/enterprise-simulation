package br.gov.sp.fatec.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.util.Date;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.sp.fatec.empresa.Empresa;
import br.gov.sp.fatec.projetofatecb.models.Usuario;

public class JwtUtils {

    private static final String KEY = "spring.jwt.sec";

    public static String generateToken(Empresa empresa) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        empresa.setSenha(null);
        String usuarioJson = mapper.writeValueAsString(empresa);
        Date agora = new Date();
        Long hora = 1000L * 60L * 60L; // Uma hora
        return Jwts.builder().claim("userDetails", usuarioJson)
            .setIssuer("br.gov.sp.fatec")
            .setSubject(empresa.getEmail())
            .setExpiration(new Date(agora.getTime() + hora))
            .signWith(SignatureAlgorithm.HS512, KEY)
            .compact();
    }

    public static Empresa parseToken(String token) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String credentialsJson = Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("userDetails", String.class);
        return mapper.readValue(credentialsJson, Empresa.class);
    }

}
