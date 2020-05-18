package com.example.projectointegrador.view;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectointegrador.R;
import com.example.projectointegrador.model.Track;

import org.w3c.dom.Text;


public class DetailTrackFragment extends Fragment {

    public static final String KEY_DETAIL_TRACK = "track";

    private ImageView imageViewImagenTrack;
    private TextView textViewNombreDelTrack;
    private TextView textViewDuracionDelTrack;
    private TextView textViewNombreArtistDelTrack;
    private TextView textViewNombreAlbumDelTrack;


    /**
     * Nuevo constructor. Setea el fragment segun el {@param track} parametro
     * @return
     */
    public static DetailTrackFragment crearDetailTrackFragment(Track track){
        DetailTrackFragment fragment = new DetailTrackFragment();
        Bundle datosDeTrack = new Bundle();
        datosDeTrack.putSerializable("track", track);
        fragment.setArguments(datosDeTrack);
        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_track, container, false);

        findViews(view);

        Bundle bundle = getArguments();
        Track trackRecibido = (Track) bundle.getSerializable(KEY_DETAIL_TRACK);

        setResources(trackRecibido);

        return view;
    }

    /**
     * setea los recursos recibidos del pojo en los elementos xml correspondientes
     * @param trackRecibido
     */
    private void setResources(Track trackRecibido) {
        imageViewImagenTrack.setImageResource(trackRecibido.getAlbum().getCover());
        textViewNombreDelTrack.setText(String.format(getString(R.string.template_titulo), trackRecibido.getTitle()));
        textViewDuracionDelTrack.setText(String.format(getString(R.string.template_duracion), trackRecibido.getDuration().toString()+" Segundos"));
        textViewNombreAlbumDelTrack.setText(String.format(getString(R.string.template_album), trackRecibido.getAlbum().getTitle()));
        textViewNombreArtistDelTrack.setText(String.format(getString(R.string.template_artista), trackRecibido.getArtist().getName()));
    }

    /**
     * setea las views en el fragment detalle
     * @param view
     */
    private void findViews(View view) {
        imageViewImagenTrack = view.findViewById(R.id.fragmentDetailTrack_ImagenviewImagenDelTrack);
        textViewNombreDelTrack = view.findViewById(R.id.fragmentDetailTrack_TextviewNombreDelTrack);
        textViewDuracionDelTrack = view.findViewById(R.id.fragmentDetailTrack_TextviewDuracionDelTrack);
        textViewNombreArtistDelTrack = view.findViewById(R.id.fragmentDetailTrack_TextviewArtistaDelTrack);
        textViewNombreAlbumDelTrack = view.findViewById(R.id.fragmentDetailTrack_TextviewAlbumDelTrack);
    }
}
