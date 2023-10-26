package com.meteor.meteorproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

class Video {
    String title;
    int thumbnailResource;
    String duration;
    int videoResource;

    public Video(String title, int thumbnailResource, String duration, int videoResource) {
        this.title = title;
        this.thumbnailResource = thumbnailResource;
        this.duration = duration;
        this.videoResource = videoResource;
    }

    public int getVideoResource() {
        return videoResource;
    }
    // Add getters here if needed
}
class VideoAdapter extends ArrayAdapter<Video> {

    public VideoAdapter(Context context, List<Video> videos) {
        super(context, 0, videos);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_list_item, parent, false);
        }

        Video currentVideo = getItem(position);

        ImageView largeThumbnail = convertView.findViewById(R.id.largeThumbnail);
        TextView largeTitle = convertView.findViewById(R.id.largeTitle);
        ImageView smallThumbnail = convertView.findViewById(R.id.smallThumbnail);
        TextView smallTitle = convertView.findViewById(R.id.smallTitle);

        if (position == 0) {  // If it's the first item
            largeThumbnail.setImageResource(currentVideo.thumbnailResource);
            largeTitle.setText(currentVideo.title);

            largeThumbnail.setVisibility(View.VISIBLE);
            largeTitle.setVisibility(View.VISIBLE);

            smallThumbnail.setVisibility(View.GONE);
            smallTitle.setVisibility(View.GONE);
        } else {
            smallThumbnail.setImageResource(currentVideo.thumbnailResource);
            smallTitle.setText(currentVideo.title);

            largeThumbnail.setVisibility(View.GONE);
            largeTitle.setVisibility(View.GONE);

            smallThumbnail.setVisibility(View.VISIBLE);
            smallTitle.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}

public class PlayerFragment extends Fragment {

    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private float currentVolume = 1.0f;
    private MediaController mediaController;
    private List<Video> videos;

    String unityServerIp = "192.168.0.187"; // Set the IP address dynamically

    private boolean isLocked = false;  // Add this to keep track of the lock state
    private boolean isPlaying = true;  // to keep track of play/pause state

    private boolean isSwitchOn = false;  // default state is off

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player, container, false);

        // Initialize VideoView
        videoView = view.findViewById(R.id.videoPlayer);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaController = new MediaController(requireContext());
        videoView.setMediaController(mediaController);

        videos = new ArrayList<>();
        videos.add(new Video("Lecture 3", R.raw.v1, "5:20",R.raw.video1));
        videos.add(new Video("Lecture 2", R.raw.v2, "5:20",R.raw.video2));
        videos.add(new Video("Lecture 3", R.raw.v3, "5:20",R.raw.video3));

        // Initialize the ListView and set an adapter
        ListView listView = view.findViewById(R.id.videoListView);
        VideoAdapter adapter = new VideoAdapter(requireContext(), videos);
        listView.setAdapter(adapter);

        // Handle item clicks in the ListView
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Video selectedVideo = videos.get(position);
            playVideo(selectedVideo);
        });

        // Get the video file's resource identifier from the "res/raw" folder
        int videoRawResourceId = R.raw.video3; // Replace "your_video" with your actual video file name

        // Set the video URI from the resource identifier
        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + videoRawResourceId;
        videoView.setVideoURI(Uri.parse(videoPath));

        try {
            mediaPlayer.setDataSource(requireActivity(), Uri.parse(videoPath));
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start playing the video
        mediaPlayer.start();

        // Start playing the video
        videoView.start();

        return view;
    }

    // Play the selected video in the VideoView
    private void playVideo(Video video) {
        videoView.pause();
        int videoRawResourceId = video.getVideoResource();
        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + videoRawResourceId;
        videoView.setVideoPath(videoPath);
        videoView.start();
    }
    // Volume seekBar functionality
    private void updateVolume() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(currentVolume, currentVolume);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private final View.OnClickListener lockIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isLocked) {
                ((ImageButton) v).setImageResource(R.drawable.baseline_lock_open_24);
                setControlsState(v.getRootView(), true);
            } else {
                ((ImageButton) v).setImageResource(R.drawable.baseline_lock_24);
                setControlsState(v.getRootView(), false);
            }
            isLocked = !isLocked;
        }
    };

    private void setControlsState(View view, boolean enabled) {
        float alpha = enabled ? 1.0f : 0.6f;

        // List of controls to change state
        int[] controls = new int[]{
                R.id.backgroundView, R.id.liveLabel, R.id.progressBar,
                R.id.iconsContainer, R.id.seekButtonsLayout, R.id.seekBar,
                R.id.buttonVolumeRow, R.id.volumeSlider
        };

        for (int id : controls) {
            View control = view.findViewById(id);
            control.setAlpha(alpha);
            control.setEnabled(enabled);
            control.setClickable(enabled);

            if (control instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) control).getChildCount(); i++) {
                    View child = ((ViewGroup) control).getChildAt(i);
                    child.setEnabled(enabled);
                    child.setClickable(enabled);
                }
            }
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final com.google.android.material.progressindicator.LinearProgressIndicator progressBar = view.findViewById(R.id.progressBar);
        final com.google.android.material.slider.Slider seekBar = view.findViewById(R.id.seekBar);
        final LinearLayout buttonLayout = view.findViewById(R.id.buttonLayout);
        final Button syncNowButton = view.findViewById(R.id.syncNowButton);
        final Button cancelButton = view.findViewById(R.id.cancelButton);

        // Store the original value of the Slider to reset it when "Cancel" is clicked
        final float[] originalValue = {seekBar.getValue()};

        // When the value of the Slider changes, show the buttons
        seekBar.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {  // Only act on user-driven changes
                    buttonLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        //"LOCK ICON" TOGGLE FUNCTIONALITY
        final ImageButton lockIcon = view.findViewById(R.id.lockIcon);
        lockIcon.setOnClickListener(lockIconClickListener);

        // "PLAY/PAUSE" TOGGLE FUNCTIONALITY
        final ImageButton playIcon = view.findViewById(R.id.playIcon);
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    playIcon.setImageResource(R.drawable.round_play_arrow_24);
                    sendStringToUnity(unityServerIp, "pause");
                    videoView.pause(); // Pause the video
                } else {
                    playIcon.setImageResource(R.drawable.baseline_pause_24);
                    sendStringToUnity(unityServerIp, "play");
                    videoView.start(); // Start or resume the video
                }
                isPlaying = !isPlaying;
            }
        });

        // SEE THROUGH VR TOGGLE SWITCH
        final ImageButton customSwitchButton = view.findViewById(R.id.customSwitchButton);
        customSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitchOn) {
                    customSwitchButton.setImageResource(R.drawable.off_state_seethroughvr);  // Replace with your off-state drawable's name
                } else {
                    customSwitchButton.setImageResource(R.drawable.on_state_seethroughvr);
                }
                isSwitchOn = !isSwitchOn;
            }
        });

        // Replay logic (for this example, I'm just resetting to the replay icon, you can add actual video replay logic)
        final ImageButton replayIcon = view.findViewById(R.id.replayIcon);
        replayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the icon (or actually replay your media content)
                replayIcon.setImageResource(R.drawable.baseline_replay_10_24);
                int currentPosition = videoView.getCurrentPosition();
                int newPosition = Math.max(currentPosition - 10000, 0); // Rewind by 10 seconds (10,000 milliseconds)
                videoView.seekTo(newPosition);
                // If you have video controls, add your replay logic here
            }
        });

        // Video player seekBar functionality
        seekBar.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {  // Only act on user-driven changes
                    buttonLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // Add functionality to seek to a specific time in the video
        seekBar.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // When the user starts touching the slider, you can add code here if needed
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // When the user stops touching the slider, seek the video to the selected time
                float selectedTime = slider.getValue(); // The selected time in percentage (0.0 to 100.0)

                // Calculate the time to seek to in milliseconds (based on the video's duration)
                int videoDuration = videoView.getDuration();
                int seekTime = (int) ((selectedTime / 100) * videoDuration);

                // Seek the video to the calculated time
                videoView.seekTo(seekTime);

                // Hide the button layout
                buttonLayout.setVisibility(View.GONE);
            }
        });

        // "Sync Now" button functionality
        syncNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentValue = seekBar.getValue();
                progressBar.setProgressCompat((int) currentValue, true);
                originalValue[0] = currentValue;
                buttonLayout.setVisibility(View.GONE);
            }
        });

        // "Cancel" button functionality
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setValue(originalValue[0]);
                buttonLayout.setVisibility(View.GONE);
            }
        });
    }

    public void sendStringToUnity(String serverIp, String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int serverPort = 8099; //Port
                    Socket socket = new Socket(serverIp, serverPort);
                    OutputStream outputStream = socket.getOutputStream();
                    byte[] messageBytes = message.getBytes("UTF-8");
                    outputStream.write(messageBytes);
                    outputStream.close();
                    socket.close();

                    Log.d("SendToUnity", "Message sent to Unity: " + message);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("SendToUnity", "Error sending message to Unity: " + e.getMessage());

                }
            }
        }).start();
    }
}