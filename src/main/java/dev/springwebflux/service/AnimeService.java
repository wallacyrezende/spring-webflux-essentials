package dev.springwebflux.service;

import dev.springwebflux.domain.Anime;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AnimeService {

  Flux<Anime> findAll();

  Mono<Anime> findById(Integer id);

  Mono<Anime> save(Anime anime);

  Mono<Void> update(Anime anime);

  Mono<Void> delete(int id);

  Flux<Anime> saveAll(List<Anime> animes);
}
