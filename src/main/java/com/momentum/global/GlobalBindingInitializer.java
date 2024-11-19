package com.momentum.global;

import com.momentum.domain.Status;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

@ControllerAdvice
public class GlobalBindingInitializer {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Status.class, new PropertyEditorSupport() {

            // 문자열을 받아서 Status로 변환
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(Status.from(text)); // 문자열 -> Status 변환
            }
        });
    }
}
