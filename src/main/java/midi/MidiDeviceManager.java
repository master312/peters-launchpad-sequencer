package midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.List;

// Used to keep track of all MidiDeviceWrappers
// TODO: Keep alive checks, and remove (or reconnect?) failed/disabled devices
public class MidiDeviceManager {
    public static MidiDeviceManager instance = new MidiDeviceManager();

    private final List<MidiDeviceWrapper> deviceWrappers = new ArrayList<>();

    public MidiDeviceManager() {
    }

    // Gets device wrapper for specific midi device info
//    public MidiDeviceWrapper OpenDevice(MidiDevice.Info info) {
//        MidiDevice midiDevice;
//        try {
//            midiDevice = MidiSystem.getMidiDevice(info);
//        } catch (MidiUnavailableException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        for (MidiDeviceWrapper wrapper : this.deviceWrappers) {
//            if (wrapper.getMidiDevice() == midiDevice)
//            {
//                return wrapper;
//            }
//        }
//
//        MidiDeviceWrapper newDevice;
//        try {
//            newDevice = new MidiDeviceWrapper(midiDevice);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        try {
//            midiDevice.open();
//        } catch (MidiUnavailableException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        this.deviceWrappers.add(newDevice);
//        return newDevice;
//    }
}
