package controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GuestBookErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";
            } else {
                model.addAttribute("errorcode", statusCode);
                model.addAttribute("errordetails", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
                return "error";
            }
        }
        model.addAttribute("errorcode", "null");
        model.addAttribute("errordetails", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}