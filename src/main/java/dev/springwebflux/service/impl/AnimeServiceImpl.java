package dev.springwebflux.service.impl;

import dev.springwebflux.domain.Anime;
import dev.springwebflux.repository.AnimeRepository;
import dev.springwebflux.service.AnimeService;
import io.netty.util.internal.StringUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeServiceImpl implements AnimeService {

  private final AnimeRepository animeRepository;
  @Override
  public Flux<Anime> findAll() {
    return animeRepository.findAll();
  }

  @Override
  public Mono<Anime> findById(Integer id) {
    return animeRepository.findById(id)
        .switchIfEmpty(monoResponseStatusNotFoundException());
  }

  @Override
  public Mono<Anime> save(Anime anime) {
    return animeRepository.save(anime);
  }

  @Override
  public Mono<Void> update(Anime anime) {
    return findById(anime.getId())
        .flatMap(a -> animeRepository.save(anime))
        .then();
  }

  @Override
  public Mono<Void> delete(int id) {
    return findById(id)
        .flatMap(animeRepository::delete);
  }

  @Override
  @Transactional
  public Flux<Anime> saveAll(List<Anime> animes) {
    return animeRepository.saveAll(animes)
        .doOnNext(this::throwResponseStatusExceptionWhenEmptyName);
  }

  private <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
  }

  private void throwResponseStatusExceptionWhenEmptyName(Anime anime) {
    if(StringUtil.isNullOrEmpty(anime.getName()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
  }
}
