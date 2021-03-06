package com.example.projectointegrador.model;

import androidx.annotation.Nullable;

import com.example.projectointegrador.R;
import com.example.projectointegrador.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Album implements Serializable, Utils.Searchable {
    private Integer id;
    private String title;
    private String cover;
    private Artist artist;
    @SerializedName("genre_id")
    private Integer genreId;
    private List<Genre> genres;
    private List<Track> tracks;
    private String type;

    public Album() {
    }

    //constructor solo usado para crear lista hardcodeada de ultimas reproducciones. Borrar cuando se empiece a traer la data de otro lado
    public Album(Integer id, String title, String cover) {
        this.id = id;
        this.title = title;
        this.cover = cover;

    }

    public Album(Integer id, String title, String cover, Artist artist, Integer genreId, List<Genre> genres, List<Track> tracks, String type) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.artist = artist;
        this.genreId = genreId;
        this.genres = genres;
        this.tracks = tracks;
        this.type = type;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }


    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String informarTitulo() {
        return getTitle();
    }

    @Override
    public String informarImagen() {
        return getCover();
    }

    @Override
    public String informarDescripcion() {
        return "Album";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Album){
            Album albumAComparar = (Album) obj;
            if(this.id.equals(albumAComparar.id)){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
}

