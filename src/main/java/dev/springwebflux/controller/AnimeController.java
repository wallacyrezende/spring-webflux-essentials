package dev.springwebflux.controller;

import dev.springwebflux.domain.Anime;
import dev.springwebflux.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
@SecurityScheme(
    name = "Basic Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
public class AnimeController {
  private final AnimeService animeService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "List all animes", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
  public Flux<Anime> listAll() {
    return animeService.findAll();
  }

  @GetMapping(path = "{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Find a anime by id", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
  public Mono<Anime> findById(@PathVariable Integer id) {
    return animeService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a anime", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
  public Mono<Anime> save(@Valid @RequestBody Anime anime) {
    return animeService.save(anime);
  }

  @PostMapping("batch")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a anime list", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
  public Flux<Anime> saveBatch(@RequestBody List<Anime> animes) {
    return animeService.saveAll(animes);
  }

  @PutMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Update a anime", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
  public Mono<Void> update(@Valid @PathVariable int id, @RequestBody Anime anime) {
    return animeService.update(anime.withId(id));
  }

  @DeleteMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a anime", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
  public Mono<Void> delete(@PathVariable int id) {
    return animeService.delete(id);
  }
}
