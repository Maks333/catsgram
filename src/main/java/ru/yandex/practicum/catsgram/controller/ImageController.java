package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.catsgram.service.ImageService;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    
}
