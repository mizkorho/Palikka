
import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

class PlayMidi
{
	private Sequence sequence;
	private Sequencer sequencer; 

	public PlayMidi(String polku)
	{
		try 
		{
			sequence = MidiSystem.getSequence(new File(polku));
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.start();
		} 
		catch (IOException e) 
		{ 
			System.out.println("IOException");
		} 
		catch (MidiUnavailableException e) 
		{
			System.out.println("Midi Unavailable: " + polku);
		} 
		catch (InvalidMidiDataException e) 
		{
			System.out.println("InvalidMidiData");
			System.out.println("Format might not be supported");
			System.out.println("Convert from RIFF Midi Format to non-RIFF?");
		}
	} 
	
	public void stop()
	{
		sequencer.stop();
	}
}