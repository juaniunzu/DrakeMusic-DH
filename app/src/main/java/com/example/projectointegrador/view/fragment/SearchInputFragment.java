package com.example.projectointegrador.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectointegrador.controller.AlbumController;
import com.example.projectointegrador.controller.ArtistController;
import com.example.projectointegrador.controller.TrackController;
import com.example.projectointegrador.databinding.FragmentSearchInputBinding;
import com.example.projectointegrador.model.Album;
import com.example.projectointegrador.model.Artist;
import com.example.projectointegrador.model.Busqueda;
import com.example.projectointegrador.model.Track;
import com.example.projectointegrador.service.ResponseAlbum;
import com.example.projectointegrador.service.ResponseArtist;
import com.example.projectointegrador.service.ResponseTrack;
import com.example.projectointegrador.util.ResultListener;
import com.example.projectointegrador.view.adapter.AlbumSearchAdapter;
import com.example.projectointegrador.view.adapter.ArtistSearchAdapter;
import com.example.projectointegrador.view.adapter.TrackSearchAdapter;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.util.List;
import java.util.Objects;

import static com.example.projectointegrador.view.fragment.SearchDetailFragment.TYPE_ALBUM;
import static com.example.projectointegrador.view.fragment.SearchDetailFragment.TYPE_ARTIST;
import static com.example.projectointegrador.view.fragment.SearchDetailFragment.TYPE_TRACK;


public class SearchInputFragment extends Fragment implements
        AlbumSearchAdapter.AlbumSearchAdapterListener,
        ArtistSearchAdapter.ArtistSearchAdapterListener,
        TrackSearchAdapter.TrackSearchAdapterListener {

    private String query;
    private SearchInputFragmentListener listener;
    private static final String LIMIT_RESULTS = "2";
    public static final String KEY_BUSQUEDA = "busqueda";

    private FragmentSearchInputBinding binding;

    public SearchInputFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (SearchInputFragmentListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchInputBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //por defecto, los recyclers no son visibles
        binding.fragmentSearchInputTextViewTracks.setVisibility(View.GONE);
        binding.fragmentSearchInputTextViewAlbums.setVisibility(View.GONE);
        binding.fragmentSearchInputTextViewArtistas.setVisibility(View.GONE);

        // listener del search
        binding.fragmentSearchInputSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            AlbumController albumController = new AlbumController();
            ArtistController artistController = new ArtistController();
            TrackController trackController = new TrackController();


            //metodo para obtener la query que introduce el usuario, al presionar el boton
            //buscar por el momento solo actualiza la query y cierra el teclado
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchInputFragment.this.query = query;
                Busqueda busqueda = new Busqueda(query);
                listener.agregarBusquedaAlHistorial(busqueda);
                return false;
            }

            //metodo que devuelve un String cada vez que el user tipea. Hace visibles los Recyclers,
            //y los actualiza con la nueva consulta basada en ese string
            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;

                albumController.buscarAlbumes(getContext(), newText, LIMIT_RESULTS, new ResultListener<ResponseAlbum>() {
                    @Override
                    public void finish(ResponseAlbum resultado) {
                        AlbumSearchAdapter albumSearchAdapter = new AlbumSearchAdapter(resultado.getAlbumes(), false, SearchInputFragment.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        binding.fragmentSearchInputRecyclerViewAlbums.setLayoutManager(linearLayoutManager);
                        binding.fragmentSearchInputRecyclerViewAlbums.setAdapter(albumSearchAdapter);
                        if (albumSearchAdapter.getItemCount() != 0){
                            binding.fragmentSearchInputTextViewAlbums.setVisibility(View.VISIBLE);
                        }
                        else {
                            binding.fragmentSearchInputTextViewAlbums.setVisibility(View.GONE);
                        }
                    }
                });

                artistController.buscarArtistas(getContext(), newText, LIMIT_RESULTS, new ResultListener<ResponseArtist>() {
                    @Override
                    public void finish(ResponseArtist resultado) {
                        ArtistSearchAdapter artistSearchAdapter = new ArtistSearchAdapter(resultado.getArtistas(), false, SearchInputFragment.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        binding.fragmentSearchInputRecyclerViewArtists.setAdapter(artistSearchAdapter);
                        binding.fragmentSearchInputRecyclerViewArtists.setLayoutManager(linearLayoutManager);
                        if (artistSearchAdapter.getItemCount() != 0){
                            binding.fragmentSearchInputTextViewArtistas.setVisibility(View.VISIBLE);
                        }
                        else {
                            binding.fragmentSearchInputTextViewArtistas.setVisibility(View.GONE);
                        }
                    }
                });

                trackController.buscarTracks(getContext(), newText, LIMIT_RESULTS, new ResultListener<ResponseTrack>() {
                    @Override
                    public void finish(ResponseTrack resultado) {
                        TrackSearchAdapter trackSearchAdapter = new TrackSearchAdapter(resultado.getTracks(), false, SearchInputFragment.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        binding.fragmentSearchInputRecyclerViewTracks.setAdapter(trackSearchAdapter);
                        binding.fragmentSearchInputRecyclerViewTracks.setLayoutManager(linearLayoutManager);
                        if (trackSearchAdapter.getItemCount() != 0){
                            binding.fragmentSearchInputTextViewTracks.setVisibility(View.VISIBLE);
                        }
                        else {
                            binding.fragmentSearchInputTextViewTracks.setVisibility(View.GONE);
                        }
                    }
                });

                return false;
            }
        });

        //listeners de los TV. Filtra la busqueda segun el textview clickeado y muestra una lista
        // completa de resultados segun lo que escriba el user (pega un searchdetailfragment
        // pasandole la query del searchview)

        binding.fragmentSearchInputTextViewArtistas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (query.length() != 0) {
                    listener.onClickFiltroVerTodo(query, TYPE_ARTIST);
                }
            }
        });

        binding.fragmentSearchInputTextViewAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (query.length() != 0) {
                    listener.onClickFiltroVerTodo(query, TYPE_ALBUM);
                }
            }
        });

        binding.fragmentSearchInputTextViewTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (query.length() != 0) {
                    listener.onClickFiltroVerTodo(query, TYPE_TRACK);
                }
            }
        });

        if (getArguments() != null) {
            Busqueda busqueda = (Busqueda) getArguments().getSerializable(KEY_BUSQUEDA);
            binding.fragmentSearchInputSearchView.setQuery(busqueda.getBusqueda(), true);
        }

        return view;
    }

    @Override
    public void onClickAlbumSearchAdapter(Album album) {
        listener.onClickAlbumSearchInputFragment(album);
    }

    @Override
    public void onClickArtistSearchAdapter(Artist artist) {
        listener.onClickArtistSearchInputFragment(artist);
    }

    @Override
    public void onClickTrackSearchAdapter(Track track, List<Track> trackList) {
        listener.onClickTrackSearchInputFragment(track, trackList);
    }

    public interface SearchInputFragmentListener {
        void onClickFiltroVerTodo(String query, String type);

        void onClickAlbumSearchInputFragment(Album album);

        void onClickArtistSearchInputFragment(Artist artist);

        void onClickTrackSearchInputFragment(Track track, List<Track> trackList);

        void agregarBusquedaAlHistorial(Busqueda busqueda);
    }
}