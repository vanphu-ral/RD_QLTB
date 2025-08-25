package rd.project.qltb.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationRessource {

    @GetMapping("/user")
    public OidcUser getCurrentUser(@AuthenticationPrincipal OidcUser oidcUser) {
        return oidcUser; // Trả về thông tin user từ token
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect("http://192.168.68.90:8080/realms/QLSX/protocol/openid-connect/logout?redirect_uri=" + URLEncoder.encode("http://localhost:4200", "UTF-8"));
    }
}
