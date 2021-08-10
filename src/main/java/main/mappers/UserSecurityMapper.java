package main.mappers;

import main.model.User;
import main.model.util.Role;
import main.security.SecurityUser;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserSecurityMapper {

    private final ModelMapper modelMapper;

    private Converter<Boolean, Set<SimpleGrantedAuthority>> authorityConverter =
            b -> getRole(b.getSource()).getAuthorities();

    public UserSecurityMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(User.class, SecurityUser.class)
                .addMappings(mapper -> mapper.using(authorityConverter)
                        .map(User::isModerator, SecurityUser::setAuthorities));
    }

    public SecurityUser mapToSecurityUser(User user) {
        return modelMapper.map(user, SecurityUser.class);
    }

    public Role getRole(boolean isModerator) {
        return isModerator ? Role.MODERATOR : Role.USER;
    }

}
