package com.proyecto.app.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.app.media.domain.Album;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByName(String name);

}
