package midi;

import javax.sound.midi.MidiMessage;

public class MidiByteMessage extends MidiMessage {
    /**
     * Constructs a new {@code MidiMessage}. This protected constructor is
     * called by concrete subclasses, which should ensure that the data array
     * specifies a complete, valid MIDI message.
     *
     * @param data an array of bytes containing the complete message. The
     *             message data may be changed using the {@code setMessage} method.
     * @see #setMessage
     */
    public MidiByteMessage(byte[] data) {
        super(data);
    }

    @Override
    public Object clone() {
        return new MidiByteMessage(this.data.clone());
    }
}
