package main.servises;

import main.Constants;
import main.api.requests.LoginRequest;
import main.api.requests.UserRequest;
import main.exceptions.NoSuchUserException;
import main.model.User;
import main.repositories.UserRepository;
import main.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PostService postService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FileService fileService;

    @Value("${avatar.path}")
    private String avatarPathName;

    @Value("${root.url.path}")
    private String rootUrlPath;


    public void saveNewUser(UserRequest userRequest) {

        User user = User.builder()
                .isModerator(false)
                .regTime(new Date())
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .code("")
                .photo("")
                .build();

        userRepository.save(user);

    }

    public User getUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user with " + email + " not found"));
    }

    public User login(LoginRequest loginRequest) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
        return getUserByEmail(securityUser.getEmail());

    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

    }

    public User getUserFromSecurityContext() {
        try{
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return getUserByEmail(securityUser.getEmail());

        } catch (Exception ex) {
            return null;
        }
    }

    public void updateProfile(UserRequest userRequest, MultipartFile photo) throws Exception {

        User user = getUserFromSecurityContext();
        if(userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }

        if(userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }
        if(userRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if(userRequest.isRemovePhoto()) {
            user.setPhoto("");
        }
        if(photo != null) {
            fileService.setFile(photo);
            fileService.setUploadPathName(avatarPathName);
            fileService.uploadFile();
            fileService.resizeImageFile();
            user.setPhoto(fileService.getNewFileName());
        }
        userRepository.save(user);
    }

    public boolean sendRestoreEmail(String email) {

        User user = userRepository.findByEmail(email).orElse(null);
        if(user != null) {
            long hours = System.currentTimeMillis()/(1000*60*60);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "") + "*" + hours;
            String link = rootUrlPath + "/login/change-password/" + uuid;

            try {
                emailService.send(email,
                        Constants.RESTORE_EMAIL_TITLE,
                        String.format(Constants.RESTORE_EMAIL_MESSAGE, link));

                user.setCode(uuid);
                userRepository.save(user);
                return true;

            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    public void restorePassword(UserRequest userRequest) {
        User user = userRepository.findByCode(userRequest.getCode()).orElseThrow(NoSuchUserException::new);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);
    }
}
