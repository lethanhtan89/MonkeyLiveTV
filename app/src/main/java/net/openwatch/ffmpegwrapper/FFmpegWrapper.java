package net.openwatch.ffmpegwrapper;

import java.nio.ByteBuffer;


public class FFmpegWrapper {

    static {
        System.loadLibrary("FFmpegWrapper");
    }

    public native void setAVOptions(AVOptions jOpts);
    public native void prepareAVFormatContext(String jOutputPath);
    public native void writeAVPacketFromEncodedData(ByteBuffer jData, int jIsVideo, int jOffset, int jSize, int jFlags, long jPts);
    public native void finalizeAVFormatContext();

    /**
     * Used to configure the muxer's options.
     * Note the name of this class's fields 
     * have to be hardcoded in the native method
     * for retrieval.
     * @author davidbrodsky
     *
     */
    static public class AVOptions{
        public int videoWidth = 1280;
        public int videoHeight = 720;

        public int audioSampleRate = 44100;
        public int numAudioChannels = 1;

        // Format specific options
        public int hlsSegmentDurationSec = 10;

        public String outputFormatName = "hls";
        // TODO: Provide a Map for format-specific options
    }

}
