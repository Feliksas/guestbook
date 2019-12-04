package controller;

import java.util.HashMap;
import com.google.gson.Gson;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SCryptHashGenController {
    @RequestMapping(path = "/scrypthash", method = RequestMethod.GET, produces = "application/json")
    public String sCryptHash(@RequestParam(name = "password") String password) {
        Gson gson = new Gson();
        HashMap<String, String> response = new HashMap<>();
        response.put("password", password);
        response.put("hash", new SCryptPasswordEncoder().encode(password));
        return gson.toJson(response);
    }
}
