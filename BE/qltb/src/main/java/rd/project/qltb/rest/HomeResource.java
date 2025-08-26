package rd.project.qltb.rest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class HomeResource {

    @GetMapping("/")
    public void home(HttpServletResponse response, Authentication auth) throws IOException {
        if (auth != null && auth.isAuthenticated()) {
            response.sendRedirect("http://localhost:4200");
        } else {
            response.sendRedirect("/oauth2/authorization/keycloak"); // bắt login
        }
    }

}
