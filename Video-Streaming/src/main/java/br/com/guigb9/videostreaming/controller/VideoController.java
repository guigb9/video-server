package br.com.guigb9.videostreaming.controller;

import br.com.guigb9.videostreaming.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    @GetMapping(value = "/video/{name}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> getVideo(@PathVariable String name) throws IOException {
        videoService.convertToHLS("static/" + name+".mp4", "static/video", name+"-converted");
        Resource videoResource = new ClassPathResource("static/converted/"+name+"-converted.m3u8");

        if (!videoResource.exists()) {
            return ResponseEntity.notFound().build();
        }
        InputStream videoStream = videoResource.getInputStream();
        StreamingResponseBody responseBody = outputStream -> {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = videoStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            videoStream.close();
        };

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}
