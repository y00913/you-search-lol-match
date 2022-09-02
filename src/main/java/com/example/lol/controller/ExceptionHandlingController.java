package com.example.lol.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ExceptionHandlingController implements ErrorController {
    @RequestMapping(value = "/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());

            System.out.println(statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "/error/404";
            }

            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "/error/500";
            }
        }

        return "/error/error";
    }
}
