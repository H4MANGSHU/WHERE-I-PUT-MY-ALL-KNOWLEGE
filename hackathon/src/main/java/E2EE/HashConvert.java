package E2EE;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Converter
@Component
public class HashConvert implements AttributeConverter<String,String> {
    private static PasswordEncoder passwordEncoder;


    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        HashConvert.passwordEncoder = passwordEncoder;  // ← static inject
    }

    @Override
    public String convertToDatabaseColumn(String s) {
        if (s == null) return null;
        return passwordEncoder.encode(s);
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return s;
    }
}
