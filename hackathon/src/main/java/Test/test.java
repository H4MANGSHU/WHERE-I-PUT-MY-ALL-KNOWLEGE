package Test;

import Entity.User;
import JPA.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Service
public class test {
    private User user;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public void save(){
        User user = new User();
        user.setPassword(passwordEncoder.encode("rendi"));
        user.setUsername("billlla");
        user.setEmail("himangshu@gmail.com");
        userRepository.save(user);

    }
}
