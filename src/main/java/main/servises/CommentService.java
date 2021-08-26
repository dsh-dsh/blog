package main.servises;

import main.api.requests.CommentRequest;
import main.mappers.CommentRequestMapper;
import main.model.Comment;
import main.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentRequestMapper commentRequestMapper;
    @Autowired
    private UserService userService;


    public Comment add(CommentRequest commentRequest) {
        Comment comment = commentRequestMapper.mapToPostComment(commentRequest);
        comment.setTime(new Date());
        comment.setUser(userService.getUserFromSecurityContext());
        return commentRepository.save(comment);
    }
}
