package launchpad;

import midi.MidiByteMessage;
import midi.MidiDeviceType;
import midi.MidiDeviceWrapper;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaunchpadInstance {

    private final MidiDeviceWrapper[] devices = new MidiDeviceWrapper[2];

    private boolean isInDawSessionLayout;

    private boolean dawModeEnabled;

    private boolean isInProgrammersMode;

    private Receiver programmersModeReceiver;

    /**
     * @param dawDevice
     * @param customDevice
     */
    public LaunchpadInstance(MidiDeviceWrapper dawDevice, MidiDeviceWrapper customDevice) throws Exception {
        this.devices[0] = dawDevice;
        this.devices[1] = customDevice;

        for (MidiDeviceWrapper device : this.devices) {
            if (device.getType() != MidiDeviceType.PortInOut) {
                // TODO: Better error handling. Somehow test if device is launchpad. Not just type...
                throw new Exception("Invalid launchpad devices");
            }
        }

        // Hacky midi message receiver initialization
        dawDevice.getMidiTransmitter().setReceiver(new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                onMidiEventReceived(dawDevice, message, timeStamp);
            }

            @Override
            public void close() {
                System.out.println("Removed 1");
            }
        });

        customDevice.getMidiTransmitter().setReceiver(new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                onMidiEventReceived(customDevice, message, timeStamp);
            }

            @Override
            public void close() {
                System.out.println("Removed 2");
            }
        });
    }

    public void enableDawMode() {
        this.sendControllMessage(new byte[]{(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x0d, 0x10, 0x1, (byte) 0xf7});
        this.dawModeEnabled = true;
        System.out.println("Launchpad disabled DAW mode");
    }

    public void clearDawState() {
        this.sendControllMessage(new byte[]{(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x0d, 0x12, 0x1, 0x00, 0x01});
    }

    public void disableDawMode() {
        this.sendControllMessage(new byte[]{(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x0d, 0x10, 0x0, (byte) 0xf7});
        this.isInDawSessionLayout = false;
        this.dawModeEnabled = false;
        System.out.println("Exited DAW mode");
    }

    public void enterProgrammersMode() {
        this.sendControllMessage(new byte[]{(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x0d, 0x0e, 0x1, (byte) 0xf7});
        this.isInProgrammersMode = true;
        System.out.println("Launchpad entered PROGRAMMERS mode");
    }

    public void clearProgrammersState() {
        ColorSpecMsg msg = new ColorSpecMsg(null);
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                msg = msg.linkMessage(LaunchpadColorType.Static, this.positionToByte(i, j), (byte) 0x00);
            }
        }

        this.fireColorSpecMsg(msg);
        System.out.println("Clear programmers");
    }

    public void exitProgrammersMode() {
        this.sendControllMessage(new byte[]{(byte) 0xf0, 0x00, 0x20, 0x29, 0x02, 0x0d, 0x0e, 0x0, (byte) 0xf7});
        this.isInProgrammersMode = false;
        System.out.println("Launchpad exited PROGRAMMERS mode");
    }

    public void selectLayout(LaunchpadLayouts layout) {
        this.isInDawSessionLayout = this.dawModeEnabled && layout == LaunchpadLayouts.DawSession;
        // TODO: this.isInProgrammersMode = false ??? somehow here...

        System.out.println("Changed layout to " + layout);
    }

    /// Sends message to the device related to current layout
    public void setLayoutMessage(byte[] data) {
        MidiMessage msg = new MidiByteMessage(data);
        MidiDeviceWrapper activeDevice = this.isInDawSessionLayout ? this.getDawDevice() : this.getCustomDevice();
        activeDevice.getMidiReceiver().send(msg, -1);
    }

    public void sendControllMessage(byte[] data) {
        MidiMessage msg = new MidiByteMessage(data);
        this.getDawDevice().getMidiReceiver().send(msg, -1);
    }

    public void setColor(int x, int y, byte color, LaunchpadColorType type) {
        if (!this.isInRange(x, y)) {
            return;
        }

        byte colorMode = LaunchpadOpCodes.SetColorStatic;
        switch (type) {
            case Flashing:
                colorMode = LaunchpadOpCodes.SetColorFlashing;
                break;
            case Pulsing:
                colorMode = LaunchpadOpCodes.SetColorFading;
                break;
        }

        this.setLayoutMessage(new byte[]{colorMode, this.positionToByte(x, y), color});
    }

    public void fireColorSpecMsg(ColorSpecMsg msg) {
        List<Byte> tmpBuilder = Arrays.asList((byte) 0xf0, (byte) 0x00, (byte) 0x20, (byte) 0x29, (byte) 0x02, (byte) 0x0d, (byte) 0x03);
        tmpBuilder = new ArrayList<>(tmpBuilder);

        do {
            if (msg.getMessage() == null) {
                msg = msg.getParent();
                continue;
            }

            for (byte b : msg.getMessage()) {
                tmpBuilder.add(b);
            }

            msg = msg.getParent();
        } while (msg != null && (msg.getParent() != null || msg.getMessage() != null));

        tmpBuilder.add((byte) 0xF7);

        byte[] message = new byte[tmpBuilder.size()];
        int cnt = 0;
        for (Byte b : tmpBuilder) {
            message[cnt] = b;
            cnt++;
        }

        this.setLayoutMessage(message);
    }

    public void colorOff(int x, int y) {
        this.setColor(x, y, (byte) 0x00, LaunchpadColorType.None);
    }

    public byte positionToByte(int x, int y) {
        if (!this.isInRange(x, y)) {
            return (byte) 0;
        }

        int finalValue;
        if (this.isInDawSessionLayout || this.isInProgrammersMode) {
            finalValue = (y * 10) + x;
        } else {
            finalValue = ((y - 1) * 4);
            if (x <= 4) {
                finalValue += (x - 1);
                finalValue += 36;
            } else {
                finalValue += (x - 5);
                finalValue += 68;
            }
        }

        return (byte) finalValue;
    }

    // TODO: This method ONLY works in programmers or DAW mode...
    public int[] byteToPosition(byte value) {
        int[] res = new int[2];
        res[1] = (int) (value / 10.0f); // Y
        res[0] = (int) ((((value / 10.0f) - res[1]) + 0.05) * 10); // X
        return res;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isInRange(int x, int y) {
        return x <= 9 && x > 0 && y <= 9 && y > 0;
    }

    public void setProgrammersModeReceiver(Receiver programmersModeReceiver) {
        this.programmersModeReceiver = programmersModeReceiver;
    }

    private void onMidiEventReceived(MidiDeviceWrapper device, MidiMessage message, long timeStamp) {
        if (this.isInProgrammersMode && this.programmersModeReceiver != null) {
            this.programmersModeReceiver.send(message, timeStamp);
            return;
        }

        if (device.equals(this.getDawDevice())) {
            System.out.println("DAW msg " + Arrays.toString(message.getMessage()));
            return;
        }

        System.out.println("USER msg " + Arrays.toString(message.getMessage()));
    }

    private MidiDeviceWrapper getDawDevice() {
        return this.devices[0];
    }

    private MidiDeviceWrapper getCustomDevice() {
        return this.devices[1];
    }
}
