package com.example.projectointegrador.dao;

import com.example.projectointegrador.R;
import com.example.projectointegrador.model.Artist;
import com.example.projectointegrador.model.Track;

import java.util.ArrayList;
import java.util.List;

public abstract class ArtistDao {
    public static List<Artist> getArtists() {
        List<Artist> artistsADevolver = new ArrayList<>();

        artistsADevolver.add(new Artist(200,"Eminem", R.drawable.eminem));
        artistsADevolver.add(new Artist(202,"Iron Maiden", R.drawable.iron_maiden));
        artistsADevolver.add(new Artist(204,"La Renga", R.drawable.la_renga));
        artistsADevolver.add(new Artist(206,"Metallica", R.drawable.metallica));
        artistsADevolver.add(new Artist(208,"Queen", R.drawable.queen));

        return artistsADevolver;
    }
}