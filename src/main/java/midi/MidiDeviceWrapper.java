package midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/*
 * This class basically wraps around midi stuff, for easier interfacing with devices
 * */
public class MidiDeviceWrapper {

    // Used to transmit data to midi device
    private Transmitter midiTransmitter;

    // Used to receive data from midi device
    private Receiver midiReceiver;

    private MidiDevice device;

    MidiDeviceWrapper(MidiDevice device) throws Exception {
        this.device = device;

        try {
            this.midiTransmitter = device.getTransmitter();
        } catch (MidiUnavailableException e) {
            this.midiTransmitter = null;
        }

        try {
            this.midiReceiver = device.getReceiver();
        } catch (MidiUnavailableException e) {
            this.midiReceiver = null;
        }

        if (this.midiTransmitter == null && this.midiReceiver == null) {
            throw new Exception("Device wrapper could not get midi transmitter and receiver");
        }
    }

    public boolean IsOpen() {
        return this.device.isOpen();
    }

    public Transmitter getMidiTransmitter() {
        return midiTransmitter;
    }

    public Receiver getMidiReceiver() {
        return midiReceiver;
    }

    public MidiDevice getMidiDevice() {
        return device;
    }
}
