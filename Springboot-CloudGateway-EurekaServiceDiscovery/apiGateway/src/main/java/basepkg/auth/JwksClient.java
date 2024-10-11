package basepkg.auth;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class JwksClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public String getRsaSigningPublicKey() {
        String jwksUri = "http://localhost:8282/realms/springbootmicroservice/protocol/openid-connect/certs";
        ResponseEntity<String> response = restTemplate.getForEntity(jwksUri, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JSONObject responseBody = new JSONObject(response.getBody());
                JSONArray keys= responseBody.getJSONArray("keys");
                for (int i = 0;  i < keys.length(); i++) {
                    JSONObject key = keys.getJSONObject(i);
                    String algorithm = key.getString("alg");
                    if (algorithm.equalsIgnoreCase("RS256")) {
                        JSONArray x5cArray = key.getJSONArray("x5c");
                        if (x5cArray.length() > 0) {
                            String x5c = x5cArray.getString(0);
                            X509Certificate certificate = parseCertificate(x5c);
                            String publicKeyEncoded = Base64.getEncoder().encodeToString(certificate.getPublicKey().getEncoded());
                            return publicKeyEncoded;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private static X509Certificate parseCertificate(String base64Certificate) throws CertificateException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Certificate);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(decodedBytes));
    }

}
