
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
        int in1 = scanner.nextInt();
        int out1 = scanner.nextInt();

        int in2 = scanner.nextInt();
        int out2 = scanner.nextInt();


        MidiDeviceWrapper device1 = new MidiDeviceWrapper(devices[in1], devices[out1]);
        MidiDeviceWrapper device2 = new MidiDeviceWrapper(devices[in2], devices[out2]);

        devices[in1].open();
        devices[in2].open();
        devices[out1].open();
        devices[out2].open();

        LaunchpadInstance launchpadInstance = new LaunchpadInstance(device1, device2);
        device2.getMidiTransmitter().setReceiver(new Receiver() {
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
            if (scanner.nextInt() > 5) {
                launchpadInstance.enterDAWMode();
            } else {
                launchpadInstance.exitDawMode();
            }

            Thread.sleep(2);
        }
    }
}
