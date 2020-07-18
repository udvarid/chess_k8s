package com.donat.donchess.controller;

import java.security.Principal;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.donat.donchess.dto.User.RegisterDto;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.dto.User.UserLoginDto;
import com.donat.donchess.service.UserService;

@RestController
@RequestMapping("/api/user")
@Transactional
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //TODO mapper használata a DTO-khoz
    @GetMapping("/listOfUsers")
    public Set<UserDto> makeListOfUsers() {
        return userService.prepareList();
    }

    //TODO erre tesztet kell írni
    @GetMapping("/listOfFreeUsers")
    public Set<UserDto> makeListOfFreeUsers() {
        return userService.prepareListOfFreeUsers();
    }

    @GetMapping("/oneUser")
    public UserDto makeListOfUsers(@RequestParam String email) {
        return userService.getOneUser(email);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/confirmation")
    public ResponseEntity confirmation(@RequestParam("token") String token) {
        userService.confirmUserByToken(token);
        return new ResponseEntity(HttpStatus.OK);
    }

    //TODO session rotate és Csrf token bekötése
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        userService.login(userLoginDto, request);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/user")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }


}
