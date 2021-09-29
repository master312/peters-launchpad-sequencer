package midi;

import javax.sound.midi.*;

public class MidiDeviceInformer {

    public static MidiDevice[] getDevices() {
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        MidiDevice[] newInfos = new MidiDevice[midiDeviceInfo.length];

        int index = 0;
        for (MidiDevice.Info info : midiDeviceInfo) {
            try {
                newInfos[index] = MidiSystem.getMidiDevice(info);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }

            index++;
        }

        return newInfos;
    }

    public static MidiDeviceType getDeviceType(MidiDevice device) {
        if (device instanceof Sequencer) {
            return MidiDeviceType.Sequencer;
        } else if (device instanceof Synthesizer) {
            return MidiDeviceType.Synthesizer;
        } else {
            if (device.getMaxReceivers() != 0 && device.getMaxTransmitters() != 0) {
                return MidiDeviceType.PortInOut;
            }

            if (device.getMaxReceivers() != 0) {
                return MidiDeviceType.PortOut;
            }

            if (device.getMaxTransmitters() != 0) {
                return MidiDeviceType.PortIn;
            }
        }

        return null;
    }

    public static void printDevices() {
        MidiDevice[] deviceInfo = getDevices();
        for (MidiDevice device : deviceInfo) {
            MidiDevice.Info info = device.getDeviceInfo();
            System.out.println("************************");
            System.out.println("Device name: " + info.getName());
            System.out.println("Description: " + info.getDescription());
            System.out.println("Version: " + info.getVersion());
            System.out.println("Vendor: " + info.getVendor());
            System.out.println("Type: " + getDeviceType(device));
            System.out.println("Max transmitters: " + device.getMaxTransmitters());
            System.out.println("Min receivers: " + device.getMaxReceivers());
        }
    }
}
