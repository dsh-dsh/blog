package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.api.requests.LikeRequest;
import main.api.requests.PostRequest;
import main.model.*;
import main.repositories.PostRepository;
import main.servises.UserService;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.datasource.url = jdbc:mysql://localhost:3306/blog_test")
@SpringBootTest
@AutoConfigureMockMvc
public class ApiPostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private String pageable = "offset=0&limit=10";
    private final static String userEmail = "user@email.com";
    private final static String moderatorEmail = "mod@email.com";


    @Test
    @Sql(statements =
            {"update posts set posts.title = 'searchQuery' where posts.id = 2",
            "update posts set posts.text = 'searchQuery' where posts.id = 3"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchPostTest() throws Exception {
        String query = "searchQuery";
        this.mockMvc.perform(get("/api/post/search?query=" + query + "&" + pageable))
                .andDo(print())
                .andExpect(jsonPath("$.count").value("2"))
                .andExpect(jsonPath("$.posts.[0].id").value("2"))
                .andExpect(jsonPath("$.posts.[1].id").value("3"));
    }

    @Test
    public void getPostsByDateTest() throws Exception {
        String date = "2021-07-21";
        this.mockMvc.perform(get("/api/post/byDate?date=" + date + "&" + pageable))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    public void getPostsByTagTest() throws Exception {
        String tag = "hibernate";
        this.mockMvc.perform(get("/api/post/byTag?tag=" + tag + "&" + pageable))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    public void getPostsByIdTest() throws Exception {
        int id = 1;

        Post post = postRepository.findById(id).get();
        int viewCount = post.getViewCount();

        this.mockMvc.perform(get("/api/post/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        post = postRepository.findById(id).get();
        Assert.assertEquals(viewCount+1, post.getViewCount());
    }

    @Test
    public void getPostsByNotExistingIdTest() throws Exception {
        String id = "100";
        this.mockMvc.perform(get("/api/post/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(moderatorEmail)
    public void getPostModerationListTest() throws Exception {
        String status = "new";
        this.mockMvc.perform(get("/api/post/moderation?status=" + status + "&" + pageable))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        status = "declined";
        this.mockMvc.perform(get("/api/post/moderation?status=" + status + "&" + pageable))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        status = "accepted";
        this.mockMvc.perform(get("/api/post/moderation?status=" + status + "&" + pageable))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(15));
    }

    @Test
    @WithUserDetails(userEmail)
    public void getMyPostTest() throws Exception {

        String status = "accepted";
        User user = userService.getUserByEmail(userEmail);

        this.mockMvc.perform(get("/api/post/my?status=" + status + "&" + pageable))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.posts.[0].user.id").value(user.getId()));
    }

    @Test
    @WithUserDetails(userEmail)
    public void newPostWithBadRequestTest() throws Exception {

        String[] tags = new String[]{"mySql", "spring"};
        PostRequest request = getPostRequest("123", "toShortText", tags);

        this.mockMvc.perform(post("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("false"))
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.text").exists());

    }

    @Test
    @WithUserDetails(userEmail)
    @Sql(statements = {"delete from tag2post where post_id = (select id from posts where title = 'title')", "delete from posts where title = 'title'"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void newPostTest() throws Exception {

        int postCountBefore = postRepository.countByIsActiveAndModerationStatus(true, ModerationStatus.NEW);
        String[] tags = new String[]{"mySql", "spring"};
        PostRequest request = getPostRequest("title", "text..............................................", tags);

        this.mockMvc.perform(post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));

        int postCountAfter = postRepository.countByIsActiveAndModerationStatus(true, ModerationStatus.NEW);
        Assert.assertEquals(1, postCountAfter - postCountBefore);

    }

    @Test
    @WithUserDetails(userEmail)
    @Sql(statements = "update posts set title = 'old title', text = 'old text..........................................' where title = 'new title'",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updatePostTest() throws Exception {

        int id = 10;
        String title = "new title";
        String[] tags = new String[]{"spring", "mySql"};
        PostRequest request = getPostRequest(title, "text..............................................", tags);

        this.mockMvc.perform(put("/api/post/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));

        Post post = postRepository.findById(id).get();
        Assert.assertEquals(title, post.getTitle());

    }

    @Test
    @WithUserDetails(userEmail)
    public void updatePostWithWrongUserTest() throws Exception {

        int id = 9;
        String title = "new title";
        String[] tags = new String[]{"spring", "mySql"};
        PostRequest request = getPostRequest("new title", "text..............................................", tags);

        this.mockMvc.perform(put("/api/post/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value("false"));

    }

    @Test
    @WithUserDetails(userEmail)
    @Sql(statements =
            "update post_votes set value = -1 " +
            "where post_id = 2 and user_id = (select id from users where email = '" + userEmail + "')",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void likeAfterDislikeTest() throws Exception {

        int id = 2;
        LikeRequest request = new LikeRequest();
        request.setPostId(id);

        this.mockMvc.perform(post("/api/post/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    @WithUserDetails(moderatorEmail)
    @Sql(statements =
            "update post_votes set value = -1 " +
                    "where post_id = 2 and user_id = (select id from users where email = '" + moderatorEmail + "')",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void dislikeFailsTest() throws Exception {

        LikeRequest request = new LikeRequest();
        request.setPostId(2);

        this.mockMvc.perform(post("/api/post/dislike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("false"));

    }

    @NotNull
    private PostRequest getPostRequest(String title, String text, String[] tags) {
        PostRequest request = new PostRequest();
        request.setActive(true);
        request.setTimestamp(new Date().getTime()/1000);
        request.setTitle(title);
        request.setText(text);
        request.setTags(tags);
        return request;
    }

}
