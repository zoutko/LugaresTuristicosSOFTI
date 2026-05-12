package com.proyecto.test.touristplaceManagment.service;

import com.proyecto.test.touristPlaceManagment.domain.Album;
import com.proyecto.test.touristPlaceManagment.domain.Photo;
import com.proyecto.test.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.test.touristPlaceManagment.service.AlbumService;
import com.proyecto.test.touristPlaceManagment.service.TouristPlaceService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private TouristPlaceService touristPlaceService;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void shouldReturnPhotosFromAlbum() {


        UUID placeId = UUID.randomUUID();

        Photo photo = new Photo();

        Album album = new Album();
        album.insertPhoto(photo);

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setAlbum(album);

        when(touristPlaceService.getById(placeId))
                .thenReturn(touristPlace);


        List<Photo> result =
                albumService.getPhotos(placeId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldAddPhotoToAlbum() {


        UUID placeId = UUID.randomUUID();

        Photo photo = new Photo();

        Album album = new Album();

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setAlbum(album);

        when(touristPlaceService.getById(placeId))
                .thenReturn(touristPlace);


        TouristPlace result =
                albumService.addPhoto(
                        placeId,
                        photo
                );


        assertEquals(
                1,
                result.getAlbum().getPhotos().size()
        );
    }

    @Test
    void shouldDeletePhotoFromAlbum() {


        UUID placeId = UUID.randomUUID();

        Photo photo = new Photo();

        Album album = new Album();
        album.insertPhoto(photo);

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setAlbum(album);

        when(touristPlaceService.getById(placeId))
                .thenReturn(touristPlace);


        TouristPlace result =
                albumService.deletePhoto(
                        placeId,
                        photo
                );


        assertEquals(
                0,
                result.getAlbum().getPhotos().size()
        );
    }

    @Test
    void shouldReturnCurrentPhoto() {

        UUID placeId = UUID.randomUUID();

        Photo photo = new Photo();

        Album album = new Album();
        album.insertPhoto(photo);

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setAlbum(album);

        when(touristPlaceService.getById(placeId))
                .thenReturn(touristPlace);


        Photo result =
                albumService.getCurrentPhoto(placeId);


        assertEquals(photo, result);
    }

    @Test
    void shouldReturnNextPhoto() {


        UUID placeId = UUID.randomUUID();

        Photo firstPhoto = new Photo();
        Photo secondPhoto = new Photo();

        Album album = new Album();
        album.insertPhoto(firstPhoto);
        album.insertPhoto(secondPhoto);

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setAlbum(album);

        when(touristPlaceService.getById(placeId))
                .thenReturn(touristPlace);


        Photo result =
                albumService.nextPhoto(placeId);


        assertEquals(secondPhoto, result);
    }

    @Test
    void shouldReturnPreviousPhoto() {


        UUID placeId = UUID.randomUUID();

        Photo firstPhoto = new Photo();
        Photo secondPhoto = new Photo();

        Album album = new Album();
        album.insertPhoto(firstPhoto);
        album.insertPhoto(secondPhoto);

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setAlbum(album);

        when(touristPlaceService.getById(placeId))
                .thenReturn(touristPlace);

        album.nextPhoto();

 
        Photo result =
                albumService.previousPhoto(placeId);


        assertEquals(firstPhoto, result);
    }
}