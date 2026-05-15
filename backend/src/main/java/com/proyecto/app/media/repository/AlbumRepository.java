package com.proyecto.app.media.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.app.media.domain.Album;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    Optional<Album> findByName(String name);

}
