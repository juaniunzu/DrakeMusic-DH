package com.example.projectointegrador.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectointegrador.R;
import com.example.projectointegrador.model.Track;

import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> {

    private List<Track> listaDeTracks;
    private TrackListListener trackListListener;

    public TrackListAdapter(List<Track> listaDeTracks,TrackListListener listener) {
        this.listaDeTracks = listaDeTracks;
        this.trackListListener = listener;
    }

    @NonNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.celda_track_simple,parent,false);

        TrackListViewHolder trackListViewHolder = new TrackListViewHolder(view);
        return trackListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, int position) {
        Track track = listaDeTracks.get(position);
        holder.darValores(track);
    }

    @Override
    public int getItemCount() {
        return listaDeTracks.size();
    }

    protected class TrackListViewHolder extends RecyclerView.ViewHolder {

        private TextView celdaTrackSimpleTextViewTitulo;
        private TextView celdaTrackSimpleTextViewArtista;

        public TrackListViewHolder(@NonNull View itemView) {
            super(itemView);

            celdaTrackSimpleTextViewTitulo = itemView.findViewById(R.id.celdaTrackSimpleTextViewTitulo);
            celdaTrackSimpleTextViewArtista = itemView.findViewById(R.id.celdaTrackSimpleTextViewArtista);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Track track = listaDeTracks.get(getAdapterPosition());
                    trackListListener.onClickTrackTrackListAdapter(track, listaDeTracks);
                }
            });

        }
        public void darValores(Track track) {
            celdaTrackSimpleTextViewTitulo.setText(track.getTitle());
            celdaTrackSimpleTextViewArtista.setText(track.getArtist().getName());
        }
    }

    /**
     * Aca deberia pasar los datos que llevara al Reproductor.
     */
    public interface TrackListListener{
        void onClickTrackTrackListAdapter(Track track, List<Track> trackList);
    }
}
