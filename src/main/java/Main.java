
/*
 * TODO:
 * -- BASE --
 * - Connect to midi
 * - Interface with launchpad 'hello world'
 * -- ADVANCED --
 * - Create MIDI lib (logging, clock callback, recording, playback etc...)
 * - Create launchpad lib (light specific button, callback on button, reset)
 * - Create launchpad layout system (load layout, init layout, tick layout)
 * - Create sequencer system
 * - ???
 * - Profit?
 * */

import launchpad.LaunchpadInstance;
import midi.MidiDeviceInformer;
import midi.MidiDeviceWrapper;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        MidiDevice[] devices = MidiDeviceInformer.getDevices();
        System.out.println("Select device: ");

        for (int i = 0; i < devices.length; i++) {
            MidiDevice.Info info = devices[i].getDeviceInfo();
            System.out.println(i + ") " + info.getName() + " " + info.getDescription() + " | " + MidiDeviceInformer.getDeviceType(devices[i]));
        }

        System.out.println("-- -- -- -- -- -- -- -- -- --");
        System.out.println("Select device number (in/out & in/out): \n");

        Scanner scanner = new Scanner(System.in);
        int in1 = 6;
        int out1 = 3;

        int in2 = 7;
        int out2 = 4;


//        int in1 = scanner.nextInt();
//        int out1 = scanner.nextInt();
//
//        int in2 = scanner.nextInt();
//        int out2 = scanner.nextInt();

        MidiDeviceWrapper device1 = new MidiDeviceWrapper(devices[in1], devices[out1]);
        MidiDeviceWrapper device2 = new MidiDeviceWrapper(devices[in2], devices[out2]);

        devices[in1].open();
        devices[in2].open();
        devices[out1].open();
        devices[out2].open();

        LaunchpadInstance launchpadInstance = new LaunchpadInstance(device1, device2);
        device1.getMidiTransmitter().setReceiver(new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                System.out.println(Arrays.toString(message.getMessage()));
            }

            @Override
            public void close() {
            }
        });

        // Test
        while (true) {
            int var = scanner.nextInt();
            switch (var)
            {
                case 1:
                    launchpadInstance.enterDAWMode();
                    break;
                case 2:
                    launchpadInstance.exitDawMode();
                    break;
                case 3:
                    launchpadInstance.testColorChangeOff();
                    break;
                case 4:
                    launchpadInstance.testColorChange();
                    break;
                case 5:
                    launchpadInstance.enterProgrammerMode();
                    break;
                case 6:
                    // 00h (0): Session (only selectable in DAW mode)
                    launchpadInstance.changeMode((byte) 0x00);
                    break;
                case 7:
                    //04h (4): Custom mode 1 (Drum Rack by factory default)
                    launchpadInstance.changeMode((byte) 0x04);
                    break;
                case 8:
                    // 05h (5): Custom mode 2 (Keys by factory default)
                    launchpadInstance.changeMode((byte) 0x05);
                    break;
                case 9:
                    // 0Dh (13): DAW Faders (only selectable in DAW mode)
                    launchpadInstance.changeMode((byte) 0x0D);
                    break;
                case 10:
                    // 06h (6): Custom mode 3 (Lighting mode in Drum Rack layout by factory default)
                    launchpadInstance.changeMode((byte) 0x06);
                    break;
                case 11:
                    // 7Fh (127): Programmer mode
                    launchpadInstance.changeMode((byte) 0x7F);
                    break;
            }

            Thread.sleep(2);
        }
    }
}
