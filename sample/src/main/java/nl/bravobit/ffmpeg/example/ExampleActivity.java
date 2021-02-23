package nl.bravobit.ffmpeg.example;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import java.io.File;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFprobe;
import timber.log.Timber;

/**
 * Created by Brian on 11-12-17.
 */
public class ExampleActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    String TAG = ">>>>";
    AppCompatTextView txtProgress;

    private void versionFFmpeg() {
        FFmpeg.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                Timber.d(message);
            }

            @Override
            public void onProgress(String message) {
                Timber.d(message);
            }
        });

    }

    private void versionFFprobe() {
        Timber.d("version ffprobe");
        FFprobe.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                Timber.d(message);
            }

            @Override
            public void onProgress(String message) {
                Timber.d(message);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_example);

        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            versionFFmpeg();
            //ffmpegTestTaskQuit();
        } else {
            // ffmpeg is not supported
            Timber.e("ffmpeg not supported!");
        }

        if (FFprobe.getInstance(this).isSupported()) {
            // ffprobe is supported
            versionFFprobe();
        } else {
            // ffprobe is not supported
            Timber.e("ffprobe not supported!");
        }


        txtProgress = findViewById(R.id.txtProgress);
        findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommand();
            }
        });

    }

    String outPutPath = "";

    public void initCommand() {


        Log.e(TAG, "initCommand: ");
        FFmpeg ffmpeg = FFmpeg.getInstance(this);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "sample.mp4");
        File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "output.mp4");
        File gif1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "test1.gif");
        File gif2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "cartoon.gif");
        outPutPath = output.getPath();


        final String cmd1[] = {
                "-y"
                , "-i"
                , file.getPath()
                , "-ss"
                , "0"
                , "-t"
                , "14"
                , "-ignore_loop"
                , "0"
                , "-i"
                , gif1.getPath()
                , "-filter_complex"
                , "overlay=x=100:y=200"
                , "-frames:v"
                , "900"
                , output.getPath()
        };


        String cmd5[] = {"-i", "VideoBefore.mp4", "-i", "MainVideo.mp4", "-i", "VideoAfter.mp4", "-i", "Audio.mp3", "-filter_complex",
                "[1:v]scale=-2:480,setsar=sar=1[Scaled];" +
                        "[0:v][Scaled][2:v]concat=n=3:v=1:a=0[Merged] -map [Merged] -map 3:a OUTPUT.mp4"};


        String imageToVideo[] = {"-loop", "1", "-i", file.getPath(), "-c:v libx264", "-t 15", "-pix_fmt", "yuv420p", "-vf", "scale=320:240", outPutPath};
/*
        String cmd2[] = {
                "-y"
                , "-i", file.getPath()
                , "-ignore_loop", "0",
                "-i", gif2.getPath(),
                "-ignore_loop", "0",
                "-i", gif2.getPath(),
                "-filter_complex",
                        "[1]scale=100:300 [v1]" + "[2][v1]scale=100:300 [v2];" +
                        "[3][v2]overlay=x=100:y=200 [v3]" +"[4][v3]overlay=x=100:y=200;"
                , "-frames:v", "200"
                , "-codec:a"
                , "copy"
                , "-codec:v"
                , "libx264"
                , output.getPath()
        };*/
//  -i input1.avi -i input2.mkv -filter_complex
//  [0:v]scale=1920:1080:force_original_aspect_ratio=decrease,pad=1920:1080:(ow-iw)/2:(oh-ih)/2[v0];
//  [v0][0:a][1:v]scale=1920:1080:force_original_aspect_ratio=decrease,pad=1920:1080:(ow-iw)/2:(oh-ih)/2[v1];
//
//  [v1][1:a]concat=n=2:v=1:a=1[outv][outa] -map [outv] -map [outa] -vcodec libx264 -crf 27 -preset ultrafast out.mp4
//


        String[] cmd = {"-i", file.getPath(), "-ignore_loop", "1",
                "-i", gif1.getPath(),
                "-i", gif2.getPath(),
                "-i", gif1.getPath(),
                "-i", gif2.getPath(),
                "-i", gif1.getPath(),
                "-filter_complex",
                "[0][1]overlay=x=W-w:y=0[tr];" +
                        "[tr][2]overlay=x=0:y=0[tl];" +
                        "[tl][3]overlay=x=W-w:y=H-h[br];" +
                        "[br][4]overlay=x=0:y=H-h[bl];" +
                        "[bl][5]overlay=x=(W-w)/2:y=(H-h)/2" +
                        ":shortest=1",
                outPutPath};



/*   // two gif 14 second video working

        String cmd2[] = {
                "-y"
                , "-i", file.getPath()
                , "-ignore_loop", "0",
                "-i", gif1.getPath(),
                "-ignore_loop", "0",
                "-i", gif2.getPath(),
                "-filter_complex",
                        "[0][1]overlay=x=100:y=200[v1];[v1][2]overlay=x=100:y=680[v2];[v2][3]overlay=x=100:y=680"
                , "-frames:v", "400"
                , "-codec:a"
                , "copy"
                , "-codec:v"
                , "libx264"
                , output.getPath()};*/


//        "[0:v]scale=50:50[base];[1:v]scale=50:50[image1];[2:v]scale=200:200[image2];[base][image1]overlay=70:70[temp1];[temp1][image2]overlay=70:70"


        String cmd2[] = {
                "-y"
                , "-i", file.getPath()
                , "-ignore_loop", "0",
                "-i", gif1.getPath(),
                "-ignore_loop", "0",
                "-i", gif2.getPath(),
                "-ignore_loop", "0",
                "-i", gif2.getPath(),
                "-filter_complex",
                /* "[1]scale=400:400 [OVERLY];" +
                     "[0][OVERLY]overlay=x=100:y=200"*/

                        "[0:v]scale=0:0[base];" +//video
                        "[1:v]scale=100:100,rotate=8:c=none:ow=rotw(8):oh=roth(8)[image1];" +//gif 1
                        "[2:v]scale=100:100,rotate=8:c=none:ow=rotw(8):oh=roth(8)[image2];" +//gif 2
                        "[3:v]scale=100:100,rotate=16:c=none:ow=rotw(16):oh=roth(16)[image3];" +//gif 2

                        "[base][image1]overlay=100:200[temp1];" +
                        "[temp1][image2]overlay=100:400[temp2];"+
                        "[temp2][image3]overlay=100:600"

                , "-frames:v", "400"
                , "-codec:a"
                , "copy"
                , "-codec:v"
                , "libx264"
                , output.getPath()
        };






        /*

            scale two image

            "[0:v]scale=0:0[base];" +//video
                        "[1:v]scale=100:100[image1];" +//gif 1
                        "[2:v]scale=100:100[image2];" +//gif 2

                        "[base][image1]overlay=100:400[temp1];" +
                        "[temp1][image2]overlay=100:800"*/


        try {
            ffmpeg.execute(cmd2, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                    Log.e(TAG, "onSuccess: " + message);
                    txtProgress.setText("onSuccess" + message);
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                    txtProgress.setText("onProgress" + message);

                    Log.e(TAG, "onProgress: " + message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    //loading.setVisibility(GONE);
                    txtProgress.setText("onFailure  " + message);

                    Log.e(TAG, "onFailure: " + message + cmd1);
                }

                @Override
                public void onStart() {
                    super.onStart();
                    txtProgress.setText("onStart");
                    Log.e(TAG, "onStart: ");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    txtProgress.setText("onFinish");
                    Log.e(TAG, "onFinish: ");
                }
            });
        } catch (Exception e) {
        }


    }
}
