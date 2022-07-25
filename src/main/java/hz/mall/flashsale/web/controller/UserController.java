package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.service.UserService;
import hz.mall.flashsale.web.model.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    @ResponseBody
    public UserVo getUser(@RequestParam(name = "id") Integer id) {
        UserVo userVo = userService.getUserById(id);
        return userVo;
    }
}
