package main.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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

//    public PostDTO(Post post) {
//        this.id = post.getId();
//        this.timestamp = post.getTime().getTime();
//        this.user = new UserDTO(post.getUser());
//        this.title = post.getTitle();
//        this.announce = setTextToAnnounce(post.getText());
//        this.viewCount = post.getViewCount();
//
//        Set<PostComment> comments = post.getComments();
//        //System.out.println(comments.size());
//    }



}
