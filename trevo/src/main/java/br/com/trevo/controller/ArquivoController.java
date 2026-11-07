package br.com.trevo.controller;

import br.com.trevo.database.model.ArquivoEntity;
import br.com.trevo.service.ArquivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/arquivos")
@RequiredArgsConstructor
public class ArquivoController {

    private final ArquivoService arquivoService;

    @GetMapping("/{id}")
    public ResponseEntity<Resource> ver(@PathVariable Integer id) throws IOException {
        ArquivoEntity a = arquivoService.buscar(id);
        Path path = Path.of(a.getCaminho());

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .body(resource);
    }
}
