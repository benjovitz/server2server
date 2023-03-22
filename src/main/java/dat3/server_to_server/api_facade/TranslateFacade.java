package dat3.server_to_server.api_facade;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


//ADD DTO CLASSES HERE
class ChoiceDTO {


  @JsonProperty("text")
  public String text;
  @JsonProperty("index")
  public Integer index;
  @JsonProperty("logprobs")
  public Object logprobs;
  @JsonProperty("finish_reason")
  public String finishReason;


}


class TranslateDTO {


  @JsonProperty("id")
  public String id;
  @JsonProperty("object")
  public String object;
  @JsonProperty("created")
  public Integer created;
  @JsonProperty("model")
  public String model;
  @JsonProperty("choices")
  public List<ChoiceDTO> choices;
  //@JsonIgnore
  @JsonProperty("usage")
  public Usage usage;
}


class Usage {
  @JsonProperty("prompt_tokens")
  public Integer promptTokens;
  @JsonProperty("completion_tokens")
  public Integer completionTokens;
  @JsonProperty("total_tokens")
  public Integer totalTokens;
}


public class TranslateFacade {

  static final String URI = "https://api.openai.com/v1/completions";
  static final String API_KEY = "";

  RestTemplate restTemplate = new RestTemplate();

  public String translateText(String text,int maxTokens) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + API_KEY);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "text-davinci-003");
    requestBody.put("prompt", "translate this into danish: " + text);
    requestBody.put("temperature", 0.3);
    requestBody.put("max_tokens", maxTokens);
    requestBody.put("top_p", 1.0);
    requestBody.put("frequency_penalty", 0.0);
    requestBody.put("presence_penalty", 0.0);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> response = restTemplate.exchange("https://api.openai.com/v1/completions", HttpMethod.POST, entity, String.class);

    String responseBody = response.getBody();
    System.out.println(responseBody);

    ObjectMapper om = new ObjectMapper();
    TranslateDTO root = om.readValue(responseBody, TranslateDTO.class);
    return root.choices.get(0).text;


  }

  public static void main(String[] args) throws JsonProcessingException {
    TranslateFacade facade = new TranslateFacade();
    String res = facade.translateText("Hello, how are you?", 20);
    System.out.println(res);
  }

}
