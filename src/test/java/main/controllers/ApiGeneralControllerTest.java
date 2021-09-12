package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.api.requests.CommentRequest;
import main.api.requests.ModerationRequest;
import main.api.requests.UserRequest;
import main.dto.SettingsDTO;
import main.model.Comment;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.User;
import main.repositories.CommentRepository;
import main.repositories.PostRepository;
import main.repositories.UserRepository;
import main.servises.SettingsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.datasource.url = jdbc:mysql://localhost:3306/blog_test")
@SpringBootTest
@AutoConfigureMockMvc
public class ApiGeneralControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SettingsService settingsService;

    private static final String userEmail = "user@email.com";
    private static final String moderatorEmail = "mod@email.com";

    @Test
    public void getInitTest() throws Exception {
        this.mockMvc.perform(get("/api/init"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("DevPub"));
    }

    @Test
    public void getSettingsTest() throws Exception {
        this.mockMvc.perform(get("/api/settings"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MULTIUSER_MODE").exists())
                .andExpect(jsonPath("$.POST_PREMODERATION").exists())
                .andExpect(jsonPath("$.STATISTICS_IS_PUBLIC").exists());
    }

    @Test
    public void getTagsTest() throws Exception {
        this.mockMvc.perform(get("/api/tag"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").exists());
    }

    @Test
    public void getCalendarTest() throws Exception {

        List<Post> posts = postRepository.findByIsActiveAndModerationStatusOrderByTimeAsc(true, ModerationStatus.ACCEPTED);
        List<String> years = posts.stream().map(post -> post.getTime().toString().substring(0, 4)).distinct().collect(Collectors.toList());
        String year = years.get(years.size()-1);

        this.mockMvc.perform(get("/api/calendar?year=" + year))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years").isArray());
    }

    @Test
    @WithUserDetails(userEmail)
    public void addImageTest() throws Exception {

        File file = new File("src/test/resources/image.jpg");
        InputStream stream = new FileInputStream(file);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                stream);

        this.mockMvc.perform(multipart("/api/image").file(multipartFile))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/")));
    }

    @Test
    @WithUserDetails(userEmail)
    public void commentWithBadRequestTest() throws Exception {

        CommentRequest request = new CommentRequest();
        request.setParentId(100);
        request.setPostId(100);
        request.setText("comment text");

        this.mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("false"))
                .andExpect(jsonPath("$.errors.parentId").exists())
                .andExpect(jsonPath("$.errors.postId").exists())
                .andExpect(jsonPath("$.errors.text").exists());
    }

    @Test
    @WithUserDetails(userEmail)
    @Sql(statements = "delete from post_comments where text = 'test comment text....'",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addCommentTest() throws Exception {

        String text = "test comment text....";
        CommentRequest request = new CommentRequest();
        request.setParentId(1);
        request.setPostId(4);
        request.setText(text);

        this.mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        List<Comment> comments = (List<Comment>) commentRepository.findAll();
        Comment comment = comments.get(comments.size()-1);
        Assert.assertEquals(text, comment.getText());
    }



    @Test
    @WithUserDetails(moderatorEmail)
    @Sql(statements = "update posts set moderation_status = 'NEW' where id = 1",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void moderationTest() throws Exception {

        int postId = 1;
        ModerationStatus status = ModerationStatus.ACCEPTED;

        ModerationRequest request = new ModerationRequest();
        request.setId(postId);
        request.setDecision(status);

        this.mockMvc.perform(post("/api/moderation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));

        Post post = postRepository.findById(postId).get();
        Assert.assertSame(post.getModerationStatus(), status);
    }

    @Test
    @WithUserDetails(userEmail)
    @Sql(statements = "update users set photo = 'upload/some/path/image.jpg' where id = 2",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "update users set name = 'user', email = 'user@email.com' where id = 2",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void profileWithJsonTest() throws Exception {

        User user = userRepository.findByEmail(userEmail).get();
        String newName = "newNameForUser";
        String newEmail = "newemail@email.com";

        UserRequest request = new UserRequest();
        request.setName(newName);
        request.setEmail(newEmail);
        request.setRemovePhoto(true);

        this.mockMvc.perform(post("/api/profile/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));

        user = userRepository.findById(user.getId()).get();

        Assert.assertEquals(user.getEmail(), newEmail);
        Assert.assertEquals(user.getName(), newName);
        Assert.assertEquals(user.getPhoto(), "");
    }

    @Test
    @WithUserDetails(userEmail)
    @Sql(statements = "update users set photo = '' where id = 2",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void profileWithFormTest() throws Exception {

        File file = new File("src/test/resources/image.jpg");
        InputStream stream = new FileInputStream(file);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                stream);

        this.mockMvc.perform(multipart("/api/profile/my").file(multipartFile))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));

        User user = userRepository.findByEmail(userEmail).get();
        Assert.assertNotEquals("", user.getPhoto());
    }

    @Test
    @WithUserDetails(userEmail)
    public void getMyStatisticTest() throws Exception {
        this.mockMvc.perform(get("/api/statistics/my"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postsCount").exists())
                .andExpect(jsonPath("$.firstPublication").exists());
    }

    @Test
    @WithUserDetails(userEmail)
    @Sql(statements = "UPDATE global_settings SET value = 'NO' WHERE code = 'STATISTICS_IS_PUBLIC'",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "UPDATE global_settings SET value = 'YES' WHERE code = 'STATISTICS_IS_PUBLIC'",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllStatisticErrorTest() throws Exception {
        this.mockMvc.perform(get("/api/statistics/all"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(userEmail)
    public void putSettingsWithWrongUserTest() throws Exception {

        SettingsDTO request = new SettingsDTO();
        request.setMultiuserMode(true);
        request.setPostPremoderation(true);
        request.setStatisticIsPublic(true);

        this.mockMvc.perform(put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(moderatorEmail)
    @Sql(statements = {"UPDATE global_settings SET value = 'YES' WHERE code = 'MULTIUSER_MODE'",
            "UPDATE global_settings SET value = 'YES' WHERE code = 'POST_PREMODERATION'",
            "UPDATE global_settings SET value = 'YES' WHERE code = 'STATISTICS_IS_PUBLIC'"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void putSettingsTest() throws Exception {

        SettingsDTO request = new SettingsDTO();
        request.setMultiuserMode(false);
        request.setPostPremoderation(false);
        request.setStatisticIsPublic(false);

        this.mockMvc.perform(put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        SettingsDTO settings = settingsService.getGlobalSettings();
        Assert.assertFalse(settings.isMultiuserMode());
        Assert.assertFalse(settings.isStatisticIsPublic());
        Assert.assertFalse(settings.isPostPremoderation());
    }
}
