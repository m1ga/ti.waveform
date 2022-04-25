/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.waveform;

import static linc.com.amplituda.ProgressOperation.DECODING;
import static linc.com.amplituda.ProgressOperation.DOWNLOADING;
import static linc.com.amplituda.ProgressOperation.PROCESSING;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.masoudss.lib.WaveformSeekBar;
import com.masoudss.lib.utils.WaveGravity;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiFileProxy;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.view.TiUIView;

import java.io.InputStream;
import java.util.List;

import linc.com.amplituda.Amplituda;
import linc.com.amplituda.AmplitudaProgressListener;
import linc.com.amplituda.AmplitudaResult;
import linc.com.amplituda.ProgressOperation;


@RequiresApi(api = Build.VERSION_CODES.N)
@Kroll.proxy(creatableInModule = TiWaveformModule.class)
public class WaveformProxy extends TiViewProxy {
    // Standard Debugging variables
    private static final String LCAT = "WaveformProxy";
    private static final boolean DBG = TiConfig.LOGD;
    WaveformSeekBar waveformSeekBar;
    Amplituda amplituda;
    long duration = 0;

    // Constructor
    public WaveformProxy() {
        super();
    }

    @Override
    public TiUIView createView(Activity activity) {
        TiUIView view = new ExampleView(this);
        amplituda = new Amplituda(TiApplication.getAppCurrentActivity());
        view.getLayoutParams().autoFillsHeight = true;
        view.getLayoutParams().autoFillsWidth = true;
        return view;
    }

    // Handle creation options
    @Override
    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
    }

    // Methods
    @Kroll.method
    public void openFile(Object input) {
        if (waveformSeekBar != null) {
            TiBaseFile file = null;
            if (input instanceof TiFileProxy) {
                file = ((TiFileProxy) input).getBaseFile();
            } else if (input instanceof String) {
                String url = TiUrl.resolve("", (String) input, null);
                file = TiFileFactory.createTitaniumFile(new String[]{url}, false);
            }

            TiBaseFile finalFile = file;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream iostream = finalFile.getInputStream();
                        amplituda.processAudio(iostream, new AmplitudaProgressListener() {

                            @Override
                            public void onProgress(ProgressOperation operation, int progress) {
                                KrollDict kd = new KrollDict();
                                kd.put("value", progress);
                                if (operation == DOWNLOADING) {
                                    kd.put("operation", "downloading");
                                } else if (operation == DECODING) {
                                    kd.put("operation", "decoding");
                                } else if (operation == PROCESSING) {
                                    kd.put("operation", "processing");
                                }
                                fireEvent("loading", kd);
                            }
                        }).get(result -> {
                            List<Integer> amplitudesData = result.amplitudesAsList();
                            duration = result.getAudioDuration(AmplitudaResult.DurationUnit.MILLIS);
                            waveformSeekBar.setSample(amplitudesData.stream().mapToInt(Integer::intValue).toArray());
                            KrollDict kd = new KrollDict();
                            kd.put("duration", duration);
                            fireEvent("ready", kd);
                        }, exception -> {
                            Log.e(LCAT, "error: " + exception);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    @Kroll.getProperty
    @Kroll.method
    public long getDuration() {
        return duration;
    }

    @Kroll.getProperty
    @Kroll.method
    public float getProgress() {
        return waveformSeekBar.getProgress();
    }

    @Kroll.setProperty
    @Kroll.method
    public void setProgress(int position) {
        waveformSeekBar.setProgress(position);
    }

    private class ExampleView extends TiUIView {
        public ExampleView(TiViewProxy proxy) {
            super(proxy);
            // get the package name
            String pkgName = proxy.getActivity().getPackageName();
            Resources res = proxy.getActivity().getResources();
            int resId_viewHolder = res.getIdentifier("wavelayout", "layout", pkgName);
            LayoutInflater inflater = LayoutInflater.from(proxy.getActivity());
            View viewWrapper = inflater.inflate(resId_viewHolder, null);

            int resId_view = res.getIdentifier("waveformseekbar", "id", pkgName);
            waveformSeekBar = viewWrapper.findViewById(resId_view);
            waveformSeekBar.setOnProgressChanged((waveformSeekBar1, progress, fromUser) -> {
                if (fromUser) {
                    KrollDict kd = new KrollDict();
                    kd.put("percentage", progress);
                    kd.put("progress", (progress / 100) * duration);
                    fireEvent("progress", kd);
                }
            });
            setNativeView(viewWrapper);
        }

        @Override
        public void processProperties(KrollDict d) {
            super.processProperties(d);

            if (d.containsKeyAndNotNull("progressColor")) {
                waveformSeekBar.setWaveProgressColor(TiConvert.toColor(d.getString("progressColor")));
            }
            if (d.containsKeyAndNotNull("barColor")) {
                waveformSeekBar.setWaveBackgroundColor(TiConvert.toColor(d.getString("barColor")));
            }
            if (d.containsKeyAndNotNull("barWidth")) {
                waveformSeekBar.setWaveWidth(TiConvert.toFloat(d.getString("barWidth")));
            }
            if (d.containsKeyAndNotNull("barGap")) {
                waveformSeekBar.setWaveGap(TiConvert.toFloat(d.getString("barGap")));
            }
            if (d.containsKeyAndNotNull("gravity")) {
                if (d.getString("gravity").equals("top")) {
                    waveformSeekBar.setWaveGravity(WaveGravity.TOP);
                } else if (d.getString("gravity").equals("bottom")) {
                    waveformSeekBar.setWaveGravity(WaveGravity.BOTTOM);
                }
            }
        }
    }
}