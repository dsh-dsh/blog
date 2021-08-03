package main.util;

import main.dto.PostDTO;
import main.model.Post;
import main.model.PostComment;
import main.model.PostVote;
import org.jsoup.Jsoup;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PostMapper {
    private final ModelMapper modelMapper;

    private Converter<Date, Long> timestampConvereter =
            (date) -> (date.getSource().getTime() / 1000);
    private Converter<String, String> announceConverter =
            (text) -> (Jsoup.parse(text.getSource()).text())
                    .substring(0, Math.min(text.getSource().length(), 147)) + "...";
    private Converter<List<PostComment>, Integer> commentsConverter =
            (comments) -> Math.toIntExact(comments.getSource().size());
    private Converter<List<PostVote>, Integer> likesConverter
            = (votes) -> Math.toIntExact(votes.getSource().stream().filter(i -> i.getValue() > 0).count());
    private Converter<List<PostVote>, Integer> dislikesConverter
            = (votes) -> Math.toIntExact(votes.getSource().stream().filter(i -> i.getValue() < 0).count());

    public PostMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Post.class, PostDTO.class)
                .addMappings(mapper -> mapper.using(timestampConvereter).map(Post::getTime, PostDTO::setTimestamp))
                .addMappings(mapper -> mapper.using(announceConverter).map(Post::getText, PostDTO::setAnnounce))
                .addMappings(mapper -> mapper.using(commentsConverter).map(Post::getComments, PostDTO::setCommentCount))
                .addMappings(mapper -> mapper.using(likesConverter).map(Post::getVotes, PostDTO::setLikeCount))
                .addMappings(mapper -> mapper.using(dislikesConverter).map(Post::getVotes, PostDTO::setDislikeCount));
    }

    public PostDTO mapToDTO(Post post) {
        return modelMapper.map(post, PostDTO.class);
    }

}
