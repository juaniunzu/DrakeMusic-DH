package com.example.projectointegrador.view;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.projectointegrador.R;
import com.example.projectointegrador.controller.TrackController;
import com.example.projectointegrador.databinding.ActivityPlayerBinding;
import com.example.projectointegrador.model.Track;
import com.example.projectointegrador.util.DrakePlayer;
import com.example.projectointegrador.util.Playable;
import com.example.projectointegrador.util.ResultListener;
import com.example.projectointegrador.util.Utils;
import com.example.projectointegrador.view.adapter.ViewPagerAdapter;
import com.example.projectointegrador.view.fragment.PlayerFragment;
import com.example.projectointegrador.view.notification.CreateNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.seismic.ShakeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements PlayerFragment.PlayerFragmentListener, ShakeDetector.Listener, Playable {

    public static final String KEY_TRACK = "track";
    public static final String KEY_LISTA = "lista";

    private ViewPager viewPager;
    private Toolbar toolbar;
    private ArrayList<Track> trackArrayList;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private MediaPlayer audioPlayer;
    private Runnable runnable;
    private Handler handler;
    private SeekBar seekBar;
    private ImageView buttonNext;
    private ToggleButton buttonPlay;
    private ImageView buttonPrevious;
    private ToggleButton buttonRepeat;
    private ToggleButton buttonShuffle;
    private ActivityPlayerBinding binding;
    private static Boolean actividadActiva = false;
    private NotificationManager notificationManager;
    private Track trackClickeado;
    private int position = 0;
    private boolean isPlaying = false;
    private ShakeDetector shakeDetector = new ShakeDetector(this);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setViews();

        SensorManager sensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);

        shakeDetector.start(sensorManager);

        setSupportActionBar(toolbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle("");
        }

        audioPlayer = DrakePlayer.getInstance().getMediaPlayer();
        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(viewPager.getCurrentItem() < trackArrayList.size() - 1){
                    onTrackNext();
                }
            }
        });
        audioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        Intent desdeMain = getIntent();
        Bundle datosDesdeMain = desdeMain.getExtras();
        trackClickeado = (Track) datosDesdeMain.getSerializable(KEY_TRACK);
        trackArrayList = (ArrayList<Track>) datosDesdeMain.getSerializable(KEY_LISTA);
        List<Fragment> listaFragments = generarFragments(trackArrayList);

        position = trackArrayList.indexOf(trackClickeado);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), listaFragments);

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(position);


        setPlayerInicio(trackArrayList);

        agregarTrackAUltimosReproducidos(trackArrayList.get(viewPager.getCurrentItem()));


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               
                PlayerActivity.this.position = position;
                setPlayerTemaNuevo(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setListenersBotonesReproductor();

        createChannel();
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));

        Intent intent = new Intent(PlayerActivity.this, DrakePlayer.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("lista", trackArrayList);
        intent.putExtras(bundle);
        //startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        startService(intent);

    }

    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                "DrakeMusic", NotificationManager.IMPORTANCE_LOW);

        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setListenersBotonesReproductor() {
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonPlay.isChecked()) {
                    onTrackPlay();
                } else {
                    onTrackPause();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() < trackArrayList.size() - 1){
                    onTrackNext();
                }
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() > 0){
                    onTrackPrevious();
                }
            }
        });

        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonShuffle.isChecked()) {
                    buttonShuffle.setBackground(getDrawable(R.drawable.ic_shuffle_accent_24dp));
                    final int cantTemas = trackArrayList.size();
                    audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Random r = new Random();
                            int indiceTemaNuevo = r.nextInt(cantTemas);
                            while (indiceTemaNuevo == viewPager.getCurrentItem()) {
                                indiceTemaNuevo = r.nextInt(cantTemas);
                            }
                            viewPager.setCurrentItem(indiceTemaNuevo);
                        }
                    });
                } else {
                    buttonShuffle.setBackground(getDrawable(R.drawable.ic_shuffle_black_24dp));
                    audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
                audioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return true;
                    }
                });
            }
        });

        buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonRepeat.isChecked()) {
                    audioPlayer.setLooping(true);
                    buttonRepeat.setBackground(getDrawable(R.drawable.ic_repeat_accent_24dp));
                } else {
                    audioPlayer.setLooping(false);
                    buttonRepeat.setBackground(getDrawable(R.drawable.ic_repeat_black_24dp));
                }
            }
        });
    }

    private void changeSeekbar() {
        try {
            seekBar.setProgress(audioPlayer.getCurrentPosition());
            if (audioPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekbar();
                    }
                };
                handler.postDelayed(runnable, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setViews() {
        viewPager = binding.activityPlayerViewPager;
        toolbar = binding.activityPlayerToolbar;
        seekBar = binding.activityPlayerSeekbar;
        buttonNext = binding.activityPlayerButtonNext;
        buttonPlay = binding.activityPlayerButtonPlay;
        buttonPrevious = binding.activityPlayerButtonPrevious;
        buttonRepeat = binding.activityPlayerButtonRepeat;
        buttonShuffle = binding.activityPlayerButtonShuffle;
    }

    private void prepararTrackParaReproduccion(Integer ordenTrackEnLista) {
        audioPlayer.reset();
        Track track = this.trackArrayList.get(ordenTrackEnLista);
        if(Utils.hayInternet(this)){
            try {
                audioPlayer.setDataSource(this, Uri.parse(track.getPreview()));
                audioPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if(audioPlayer.isPlaying()){
                audioPlayer.stop();
                onBackPressed();
            } else {
                onBackPressed();
            }
        }

    }

    private List<Fragment> generarFragments(List<Track> listaDeTracks) {
        List<Fragment> listaADevolver = new ArrayList<>();
        for (Track track : listaDeTracks) {
            Fragment fragment = PlayerFragment.crearPlayerFragment(track, this);
            listaADevolver.add(fragment);
        }
        return listaADevolver;
    }

    private void pegarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityPlayerFragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override

    public void onClickAddTrackFavorite(final Track track, CheckBox checkBox) {
        final TrackController trackController = new TrackController();
        trackController.searchTrackFavoritos(track, firebaseUser, new ResultListener<List<Track>>() {
            @Override
            public void finish(List<Track> resultado) {
                if (resultado.contains(track)) {
                    trackController.eliminarTrackFavoritos(track, firebaseUser, new ResultListener<Track>() {
                        @Override
                        public void finish(Track resultado) {
                        }
                    });
                } else {
                    trackController.agregarTrackAFavoritos(track, firebaseUser, new ResultListener<Track>() {
                        @Override
                        public void finish(Track resultado) {
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
        runnable = null;
        seekBar = null;
        notificationManager.cancelAll();
        unregisterReceiver(broadcastReceiver);

    }

    private void agregarTrackAUltimosReproducidos(Track track) {
        TrackController trackController = new TrackController();
        trackController.agregarTrackAUltimosReproducidos(track, firebaseUser, new ResultListener<Track>() {
            @Override
            public void finish(Track resultado) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        actividadActiva = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        actividadActiva = false;
        shakeDetector.stop();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        Bundle datos = new Bundle();
        datos.putSerializable(KEY_LISTA, trackArrayList);
        datos.putSerializable(KEY_TRACK, trackArrayList.get(viewPager.getCurrentItem()));
        intent.putExtras(datos);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void hearShake() {
        //Metodo que reprodusca un tema random de la Lista.
        int cantTemas = trackArrayList.size();
        Random r = new Random();
        int indiceTemaNuevo = r.nextInt(cantTemas);
        while (indiceTemaNuevo == viewPager.getCurrentItem()) {
            indiceTemaNuevo = r.nextInt(cantTemas);
        }
        viewPager.setCurrentItem(indiceTemaNuevo);
        audioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying) {
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    @Override
    public void setPlayerTemaNuevo(Integer posicionNueva) {
        audioPlayer.reset();
        prepararTrackParaReproduccion(posicionNueva);

        handler = new Handler();

        audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (actividadActiva) {
                    seekBar.setMax(mp.getDuration());
                    onTrackPlay();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void setPlayerInicio(ArrayList<Track> trackList) {
        //position = viewPager.getCurrentItem();
        if(audioPlayer.isPlaying()){
            audioPlayer.reset();
        }
        prepararTrackParaReproduccion(position);

        handler = new Handler();

        audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (actividadActiva) {
                    seekBar.setMax(mp.getDuration());
                    onTrackPlay();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    @Override
    public void onTrackPrevious() {

        position--;
        CreateNotification.createNotification(PlayerActivity.this, trackArrayList.get(position),
                R.drawable.ic_pause_circle_filled_black_24dp,
                position, trackArrayList.size() - 1);
        viewPager.setCurrentItem(position);
        setPlayerTemaNuevo(position);

    }

    @Override
    public void onTrackPlay() {
        buttonPlay.setBackground(getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
        CreateNotification.createNotification(PlayerActivity.this, trackArrayList.get(position),
                R.drawable.ic_pause_circle_filled_black_24dp,
                position, trackArrayList.size() - 1);
        isPlaying = true;
        audioPlayer.start();
        changeSeekbar();
    }

    @Override
    public void onTrackPause() {
        buttonPlay.setBackground(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
        CreateNotification.createNotification(PlayerActivity.this, trackArrayList.get(position),
                R.drawable.ic_play_circle_filled_black_24dp,
                position, trackArrayList.size() - 1);
        isPlaying = false;
        audioPlayer.pause();
    }

    @Override
    public void onTrackNext() {
        position++;
        CreateNotification.createNotification(PlayerActivity.this, trackArrayList.get(position),
                R.drawable.ic_pause_circle_filled_black_24dp,
                position, trackArrayList.size() - 1);
       
        viewPager.setCurrentItem(position);
        setPlayerTemaNuevo(position);

    }
}
