package rd.project.qltb.rest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class HomeResource {

    @GetMapping("/")
    public void redirectToFrontend(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:4200");
    }

}
