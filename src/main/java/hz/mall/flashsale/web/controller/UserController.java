package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.service.UserService;
import hz.mall.flashsale.web.model.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get")
    @ResponseBody
    public UserVo getUser(@RequestParam(name = "id") Integer id) {
        UserVo userVo = userService.getUserById(id);
        return userVo;
    }
}
