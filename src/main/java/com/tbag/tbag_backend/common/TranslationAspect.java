package com.tbag.tbag_backend.common;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
@RequiredArgsConstructor
public class TranslationAspect {

    private final TranslationService translationService;

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object translateFields(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();
            if (body instanceof Page) {
                Page<?> page = (Page<?>) body;
                page.getContent().forEach(this::translateObject);
            } else {
                translateObject(body);
            }
            return ResponseEntity.status(responseEntity.getStatusCode()).headers(responseEntity.getHeaders()).body(body);
        } else {
            translateObject(result);
        }

        return result;
    }

    private void translateObject(Object obj) {
        if (obj == null) return;

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Trans.class)) {
                field.setAccessible(true);
                try {
                    String originalValue = (String) field.get(obj);
                    String translatedValue = translationService.translate(originalValue, Language.ofLocale());
                    field.set(obj, translatedValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
