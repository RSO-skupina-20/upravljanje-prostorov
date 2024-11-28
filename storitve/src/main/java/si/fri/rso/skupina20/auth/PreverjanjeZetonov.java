package si.fri.rso.skupina20.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class PreverjanjeZetonov {

    private static String secretKey = System.getenv("JWT_SECRET");

    // Preveri žeton in vrne id uporabnika
    public static Integer verifyToken(String token){
        try{
            token = token.replace("Bearer ", "");
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWT.require(algorithm).build().verify(token);
            DecodedJWT jwt = JWT.decode(token);
            String tipUporabnika = jwt.getClaim("tipUporabnika").asString();
            
            if(!tipUporabnika.equals("LASTNIK")){
                return -1;
            }
            return jwt.getClaim("id").asInt();
        } catch (Exception e){
            return -1;
        }
    }

    // Preveri ali je uporabnik lastnik
    public static boolean verifyOwner(String token, int id){
        try{
            Integer id2 = verifyToken(token);
            if(id2 == -1 || id2 != id){
                return false;
            }else{
                return true;
            }
        } catch (Exception e){
            return false;
        }
    }

}
