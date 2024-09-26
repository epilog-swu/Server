package com.epi.epilog.app.service.fall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapService {
    @Value("${sms.map.api}")
    private String apiKey;
    @Value("${kakao.api.key}")
    private String kakaoKey;
    @Value("${bitly.api.token}")
    private String bitlyKey;
    private final RestTemplate restTemplate;

    /**
     * Map Image 생성
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public String getMapImageUrl(double latitude, double longitude) {
        // TODO: 문자열 상수로 빼기
        return String.format(
                "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=17&size=500x300&sensor=false&markers=color:red%%7Clabel:L%%7C%f,%f&key=%s",
                latitude, longitude, latitude, longitude, apiKey
        );
    }

    /**
     * 위도/경도 -> 주소 변경
     *
     * @param latitude
     * @param longitude
     * @return
     * @throws JsonProcessingException
     */
    public String getAddress(double latitude, double longitude) throws JsonProcessingException {
        // TODO: 문자열 상수로 빼기
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + longitude + "&y=" + latitude;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(response.getBody());
        JsonNode resultsNode = rootNode.path("documents");

        if (resultsNode.isArray() && resultsNode.size() > 0) {
            JsonNode addressNode = resultsNode.get(0).path("address").path("address_name");
            return addressNode.asText();
        }
        return null;
    }

    /**
     * 단축 URL 생성
     *
     * @param longUrl
     * @return
     * @throws Exception
     */
    public String createShortURL(String longUrl) throws Exception {
        String url = "https://api-ssl.bitly.com/v4/shorten";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bitlyKey);

        Map<String, String> body = new HashMap<>();
        body.put("long_url", longUrl);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("id").asText();
        } else {
            return longUrl;
        }
    }
}