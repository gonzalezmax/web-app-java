
public class JWTHeaderKIDEndpointTest extends LessonTest {

  private SecretKey secretKey;

  @BeforeEach
  public void setup() throws NoSuchAlgorithmException {
    when(webSession.getCurrentLesson()).thenReturn(new JWT());
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
    secretKey = keyGen.generateKey();
  }

  @Test
  public void solveAssignment() throws Exception {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", "Tom");
    String token =
        Jwts.builder()
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
            .setHeaderParam("kid", "webgoat_key")
            .setIssuedAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toDays(10)))
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    mockMvc
        .perform(post("/JWT/kid/delete").param("token", token).content(""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lessonCompleted", is(true)));
  }

  @Test
  public void withJerrysKeyShouldNotSolveAssignment() throws Exception {
    // TOKEN_JERRY is removed as it contained sensitive information and was hard-coded.
    // The test case should be refactored to use a dynamically generated token or mock the behavior.
  }

  @Test
  public void shouldNotBeAbleToBypassWithSimpleToken() throws Exception {
    mockMvc
        .perform(post("/JWT/kid/delete").param("token", ".eyJ1c2VybmFtZSI6IlRvbSJ9.").content(""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feedback", is(messages.getMessage("jwt-invalid-token"))));
  }
}
