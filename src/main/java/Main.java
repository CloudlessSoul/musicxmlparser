import nu.xom.ParsingException;
import org.jfugue.integration.MusicXmlParser;
import org.jfugue.integration.MusicXmlParserListener;
import org.jfugue.midi.MidiParserListener;
import org.jfugue.parser.ParserListener;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.Token;
import org.jfugue.player.Player;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParserListener;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, ParsingException, URISyntaxException {
        playWithPatterns();
        // playWithMusicXmlParsing();
    }

    private static void playWithPatterns() throws URISyntaxException, ParserConfigurationException, ParsingException, IOException {
        var score = getFileFromResource("choralemmwa.xml");

        MusicXmlParser xmlParser = new MusicXmlParser();

        StaccatoParserListener staccatoParserListener = new StaccatoParserListener();
        xmlParser.addParserListener(staccatoParserListener);

        var midiParserListener = new MidiParserListener();
        xmlParser.addParserListener(midiParserListener);

        xmlParser.parse(score);

        Player player = new Player();
        var midiSequence = midiParserListener.getSequence();
        player.play(midiSequence);

        var pattern = staccatoParserListener.getPattern();

        var layerPatterns = extractLayers(pattern);

        var layer = layerPatterns.get("L0");
        System.out.println("Playing: " +  layer.toString());
        player.play(layer);

        layer = layerPatterns.get("L1");
        System.out.println("Playing: " +  layer.toString());
        player.play(layer);

        layer = layerPatterns.get("L4");
        System.out.println("Playing: " +  layer.toString());
        player.play(layer);

        layer = layerPatterns.get("L6");
        System.out.println("Playing: " +  layer.toString());
        player.play(layer);
    }

    private static Map<String, Pattern> extractLayers(Pattern pattern) {
        Map<String, Pattern> layerPatterns = new HashMap<>();
        System.out.println("Pattern parsed is: ");
        String currentLayer = "L0";
        Pattern currentPattern = new Pattern();
        layerPatterns.put(currentLayer, currentPattern);
        for (Token p : pattern.getTokens()) {
            if (p.getType() == Token.TokenType.LAYER) {
                currentLayer = p.getPattern().toString();
                if (!layerPatterns.containsKey(currentLayer)) {
                    layerPatterns.put(currentLayer, new Pattern());
                }
                currentPattern = layerPatterns.get(currentLayer);
            }
            if (p.getType() == Token.TokenType.NOTE && currentLayer != null && currentPattern != null) {
                currentPattern.add(p.getPattern());
            }
            System.out.println(p.getType() + ":" + p.getPattern().toString());
        }

        System.out.println("Available layers: " + layerPatterns.keySet().stream().reduce("", (layer, n) -> layer + n +  ", "));

        return layerPatterns;
    }

    private static void playWithMusicXmlParsing() throws ParserConfigurationException, URISyntaxException, ParsingException, IOException {
        MusicXmlParser xmlParser = new MusicXmlParser();
        xmlParser.addParserListener(new DebugParserListener());

        var midiParserListener = new MidiParserListener();
        xmlParser.addParserListener(midiParserListener);

        var musicXmlParserListener = new MusicXmlParserListener();
        xmlParser.addParserListener(musicXmlParserListener);

        var score = getFileFromResource("stringthing.xml");
        xmlParser.parse(score);

        Player player = new Player();
        var midiSequence = midiParserListener.getSequence();
        player.play(midiSequence);
    }


    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }

    }

    private static class DebugParserListener implements ParserListener {
        @Override
        public void beforeParsingStarts() {
            System.out.println("Before Parsing Starts");
        }

        @Override
        public void afterParsingFinished() {
            System.out.println("After Parsing Finishes");
        }

        @Override
        public void onTrackChanged(byte b) {
            System.out.println("Track change: " + b);
        }

        @Override
        public void onLayerChanged(byte b) {
            System.out.println("Layer change: " + b);
        }

        @Override
        public void onInstrumentParsed(byte b) {
            System.out.println("Instrument Parsed: " + b);
        }

        @Override
        public void onTempoChanged(int i) {
            System.out.println("Tempo Changed: " + i);
        }

        @Override
        public void onKeySignatureParsed(byte b, byte b1) {
            System.out.println("Key Signature Parsed: " + b + " " + b1);

        }

        @Override
        public void onTimeSignatureParsed(byte b, byte b1) {
            System.out.println("Time Signature Parsed: " + b + " " + b1);
        }

        @Override
        public void onBarLineParsed(long l) {
            System.out.println("Bar Line Parsed: " + l);
        }

        @Override
        public void onTrackBeatTimeBookmarked(String s) {
            System.out.println("onTrackBeatTimeBookmarked: " + s);
        }

        @Override
        public void onTrackBeatTimeBookmarkRequested(String s) {
            System.out.println("onTrackBeatTimeBookmarkRequested: " + s);
        }

        @Override
        public void onTrackBeatTimeRequested(double v) {
            System.out.println("onTrackBeatTimeRequested: " + v);
        }

        @Override
        public void onPitchWheelParsed(byte b, byte b1) {
            System.out.println("onPitchWheelParsed: " + b + " " + b1);

        }

        @Override
        public void onChannelPressureParsed(byte b) {
            System.out.println("onChannelPressureParsed: " + b);

        }

        @Override
        public void onPolyphonicPressureParsed(byte b, byte b1) {
            System.out.println("onPitchWheelParsed: " + b + " " + b1);

        }

        @Override
        public void onSystemExclusiveParsed(byte... bytes) {
            System.out.println("onChannelPressureParsed: " + bytes);

        }

        @Override
        public void onControllerEventParsed(byte b, byte b1) {
            System.out.println("onPitchWheelParsed: " + b + " " + b1);

        }

        @Override
        public void onLyricParsed(String s) {
            System.out.println("onLyricParsed: " + s);

        }

        @Override
        public void onMarkerParsed(String s) {
            System.out.println("onMarkerParsed: " + s);

        }

        @Override
        public void onFunctionParsed(String s, Object o) {
            System.out.println("onFunctionParsed: " + s);

        }

        @Override
        public void onNotePressed(Note note) {
            System.out.println("onNotePressed: " + note.getDecoratorString());

        }

        @Override
        public void onNoteReleased(Note note) {
            System.out.println("onNoteReleased: " + note.getDecoratorString());

        }

        @Override
        public void onNoteParsed(Note note) {
            System.out.println("onNoteParsed: " + note.getDecoratorString());

        }

        @Override
        public void onChordParsed(Chord chord) {
            System.out.println("onChordParsed: " + chord.toDebugString());

        }
    }
}
