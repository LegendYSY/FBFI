package CreateToken;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;


public class JWT {
	private String secretKey = "secret";

    private long validityInMilliseconds = 3600000;

    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken() {

    	init();
        Claims claims = Jwts.claims().setSubject("fdse_microservice");
        claims.put(InfoConstant.ROLES, new HashSet<>(Arrays.asList("ROLE_USER")));
        claims.put(InfoConstant.ID, UUID.fromString("4d2a46c7-71cb-4cf1-b5bb-b68406d9da6f"));

        Date now = new Date();
        Date validate = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public static void main(String[] args){
    	JWT jwt = new JWT();
    	jwt.init();
    	System.out.println(jwt.createToken());
    }
}
