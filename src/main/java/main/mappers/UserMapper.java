package main.mappers;

import main.dto.UserDTO;
import main.model.ModerationStatus;
import main.model.User;
import main.servises.PostService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private PostService postService;

    private ModelMapper modelMapper;

    private Converter<Boolean, Integer> moderationCountConverter = b -> getModerationCount(b.getSource());
    private Converter<Boolean, Boolean> settingsConverter = b -> b.getSource();

    public UserMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(User.class, UserDTO.class)
                .addMappings(m -> m.using(moderationCountConverter).map(User::isModerator, UserDTO::setModerationCount))
                .addMappings(m -> m.using(settingsConverter).map(User::isModerator, UserDTO::setSettings));
    }

    public UserDTO mapToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public int getModerationCount(boolean isModerator) {
        if(isModerator) {
            return postService.getPostCount(true, ModerationStatus.NEW);
        } else {
            return 0;
        }

    }
}
