package main.dto;

import lombok.Data;
import main.model.Post;
import main.model.PostComment;
import org.jsoup.Jsoup;

import java.util.Set;

@Data
public class PostDTO {

    private int id;
    private long timestamp;
    private UserDTO user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.timestamp = post.getTime().getTime();
        this.user = new UserDTO(post.getUser());
        this.title = post.getTitle();
        this.announce = setTextToAnnounce(post.getText());
        this.viewCount = post.getViewCount();

        Set<PostComment> comments = post.getComments();
        //System.out.println(comments.size());
    }

    public String setTextToAnnounce(String text) {
        text = Jsoup.parse(text).text();
        int newLength = Math.min(text.length(), 147);
        return text.substring(0, newLength) + "...";
    }

}
