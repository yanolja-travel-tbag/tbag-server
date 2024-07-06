package com.tbag.tbag_backend.common;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
@RequiredArgsConstructor
public class TranslationAspect {

    private final TranslationService translationService;
    private final ThreadLocal<Set<Object>> translatedObjects = ThreadLocal.withInitial(HashSet::new);

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object translateFields(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();
            searchObject(body);
            return ResponseEntity.status(responseEntity.getStatusCode()).headers(responseEntity.getHeaders()).body(body);
        } else {
            searchObject(result);
        }

        return result;
    }

    private void searchObject(Object body) {
        if (body instanceof Page) {
            Page<?> page = (Page<?>) body;
            page.getContent().forEach(this::translateObject);
        } else if (body instanceof List) {
            List<?> list = (List<?>) body;
            list.forEach(this::translateObject);
        } else if (body instanceof Map) {
            ((Map<?, ?>) body).values().forEach(this::translateObject);
        } else {
            translateObject(body);
        }
    }

    private void translateObject(Object obj) {
        if (obj == null) return;

        Set<Object> processedObjects = translatedObjects.get();
        if (processedObjects.contains(obj)) {
            return;
        }
        processedObjects.add(obj);

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Trans.class)) {
                    if (List.class.isAssignableFrom(field.getType())) {
                        translateListField(field, obj);
                    } else {
                        String originalValue = (String) field.get(obj);
                        if (originalValue != null) {
                            String translatedValue = translationService.translate(originalValue, Language.ofLocale());
                            field.set(obj, translatedValue);
                        }
                    }
                } else if ("preferredGenres".equals(field.getName())) {
                    translateGenresField(field, obj);
                } else {
                    Object fieldValue = field.get(obj);
                    if (fieldValue != null && !isPrimitiveOrWrapper(fieldValue.getClass()) && !(fieldValue instanceof String)) {
                        if (fieldValue instanceof List) {
                            translateListField(field, obj);
                        } else {
                            translateObject(fieldValue);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InaccessibleObjectException e) {
            }
        }
        processedObjects.remove(obj);
    }

    private void translateGenresField(Field field, Object obj) throws IllegalAccessException {
        Map<String, List<?>> genres = (Map<String, List<?>>) field.get(obj);
        for (List<?> genreList : genres.values()) {
            for (Object genreObj : genreList) {
                translateGenreObject(genreObj);
            }
        }
    }

    private void translateGenreObject(Object genreObj) {
        Field[] fields = genreObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if ("genreName".equals(field.getName())) {
                    String genreName = (String) field.get(genreObj);
                    if (genreName != null) {
                        String translatedValue = translationService.translate(genreName, Language.ofLocale());
                        field.set(genreObj, translatedValue);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void translateListField(Field field, Object obj) throws IllegalAccessException {
        List<?> list = (List<?>) field.get(obj);
        if (list != null) {
            if (!list.isEmpty() && list.get(0) instanceof String) {
                List<String> stringList = (List<String>) list;
                for (int i = 0; i < stringList.size(); i++) {
                    String translatedValue = translationService.translate(stringList.get(i), Language.ofLocale());
                    stringList.set(i, translatedValue);
                }
            } else {
                for (Object element : list) {
                    translateObject(element);
                }
            }
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Boolean.class || type == Byte.class || type == Character.class ||
                type == Double.class || type == Float.class || type == Integer.class ||
                type == Long.class || type == Short.class || type == String.class;
    }
}
