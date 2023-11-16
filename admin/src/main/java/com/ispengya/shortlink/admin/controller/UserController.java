package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoDTO;
import com.ispengya.shortlink.admin.service.UserService;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ispengya
 * @date 2023/11/16 16:50
 */
@RestController
@RequestMapping("/admin/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("userInfo/{username}")
    public Result<UserInfoDTO> getUserByName(@PathVariable("username") String username) {
        UserInfoDTO userInfoDTO = userService.getUserByUserName(username);
        return Results.success(userInfoDTO);
    }


}
