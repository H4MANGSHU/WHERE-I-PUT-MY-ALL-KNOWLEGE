package IPCONFIGURATION;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@NullMarked
@Component("ipFilter")
public class IpFilter {

    @Value("${IP.ADR}")
    private String  IpAddress;

    public boolean isAllowed(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        var Ip = request.getRemoteAddr();
        if(!Ip.equals(IpAddress)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
       return Ip.equals(IpAddress);
    }
}
