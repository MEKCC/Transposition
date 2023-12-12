package myapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Transposer {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Transposer <input-json-file> <semitones-to-transpose>");
            System.exit(1);
        }

        String inputFilePath = args[0];
        int semitonesToTranspose = Integer.parseInt(args[1]);

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the input JSON file
            List<List<Integer>> inputNotes = objectMapper.readValue(new File(inputFilePath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, List.class));

            // Transpose the notes
            List<List<Integer>> transposedNotes = transposeNotes(inputNotes, semitonesToTranspose);

            // Check if any note is out of range
            if (isOutOfRange(transposedNotes)) {
                System.out.println("Error: Transposed notes are out of range.");
            } else {
                // Write the transposed notes to a new JSON file
                objectMapper.writeValue(new File("output.json"), transposedNotes);
                System.out.println("Transposition completed successfully. Result saved in output.json");
            }
        } catch (IOException e) {
            System.out.println("Error reading or writing JSON file: " + e.getMessage());
        }
    }

    private static List<List<Integer>> transposeNotes(List<List<Integer>> notes, int semitonesToTranspose) {
        for (List<Integer> note : notes) {
            int octave = note.get(0);
            int noteNumber = note.get(1);

            // Transpose each note
            int transposedNote = (noteNumber + semitonesToTranspose + 12) % 12;
            note.set(1, transposedNote);

            // Adjust the octave if needed
            note.set(0, octave + (noteNumber + semitonesToTranspose + 12) / 12 - 1);
        }
        return notes;
    }

    private static boolean isOutOfRange(List<List<Integer>> notes) {
        for (List<Integer> note : notes) {
            int octave = note.get(0);
            int noteNumber = note.get(1);
            if (octave < -3 || (octave == -3 && noteNumber < 10) || octave > 5 || (octave == 5 && noteNumber > 1)) {
                return true; // Note is out of range
            }
        }
        return false; // All notes are within range
    }
}
