package launchpad;

import midi.MidiByteMessage;
import midi.MidiDeviceType;
import midi.MidiDeviceWrapper;

import javax.sound.midi.MidiMessage;

public class LaunchpadInstance extends LaunchpadBase {

    private MidiDeviceWrapper[] devices = new MidiDeviceWrapper[2];

    /**
     *
     * @param sessionDevice Device used to control session, and messages. Usually the first one on the list
     * @param midiDevice Device on which launchpad sends raw midi key presses. EX: on drums view
     */
    public LaunchpadInstance(MidiDeviceWrapper sessionDevice, MidiDeviceWrapper midiDevice) throws Exception {
        this.devices[0] = sessionDevice;
        this.devices[1] = midiDevice;

        for (MidiDeviceWrapper device : this.devices) {
            if (device.getType() != MidiDeviceType.PortInOut) {
                // TODO: Better error handling. Somehow test if device is launchpad. Not just type...
                throw new Exception("Invalid launchpad devices");
            }
        }
    }

    public void initialize() {
        this.enterDAWMode();
    }

    public void dispose() {
        this.exitDawMode();
    }

    public void enterDAWMode() {
        this.sendControllMessage(new byte[]{(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x0d, 0x10, 0x1, (byte) 0xf7});
        System.out.println("Launchpad entered DAW mode");
    }

    public void exitDawMode() {
        this.sendControllMessage(new byte[]{(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x0d, 0x10, 0x0, (byte) 0xf7});
        System.out.println("Launchpad exited DAW mode");
    }

    private void sendControllMessage(byte[] data) {
        MidiMessage msg = new MidiByteMessage(data);
        this.getControllDevice().getMidiReceiver().send(msg, -1);
    }

    private MidiDeviceWrapper getControllDevice() {
        return this.devices[0];
    }
}
