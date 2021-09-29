package midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.ArrayList;

/*
 * This class basically wraps around midi stuff, for easier interfacing with devices
 * */
public class MidiDeviceWrapper {

    // Used to receive data from Midi Ports
    private Transmitter midiTransmitter;

    // Used to send data to Midi Ports
    private Receiver midiReceiver;

    private MidiDeviceType type;

    private final ArrayList<MidiDevice> devices = new ArrayList<>();

    public MidiDeviceWrapper(MidiDevice inDevice, MidiDevice outDevice) throws MidiUnavailableException {
        this.type = MidiDeviceType.PortInOut;
        this.midiTransmitter = inDevice.getTransmitter();
        this.midiReceiver = outDevice.getReceiver();

        this.devices.add(inDevice);
        this.devices.add(outDevice);
    }

    MidiDeviceWrapper(MidiDevice device) throws Exception {
        this.type = MidiDeviceInformer.getDeviceType(device);

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

        this.devices.add(device);
    }

    public Transmitter getMidiTransmitter() {
        return midiTransmitter;
    }

    public MidiDeviceType getType() {
        return type;
    }

    public Receiver getMidiReceiver() {
        return midiReceiver;
    }

    public ArrayList<MidiDevice> getDevices() {
        return devices;
    }
}
