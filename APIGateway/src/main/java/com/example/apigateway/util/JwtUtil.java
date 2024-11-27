package com.example.apigateway.util;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private String secretKey = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";


    public void validateToken(final String token){
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
