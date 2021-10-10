
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
import launchpad.LaunchpadLayoutView;
import launchpad.LayoutManager;
import midi.MidiDeviceInformer;
import midi.MidiDeviceWrapper;

import javax.sound.midi.MidiDevice;

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

        // TODO: Automatically scan for launchpad ports
        int in1 = 4; //scanner.nextInt();
        int out1 = 2; //scanner.nextInt();
        int in2 = 5; //scanner.nextInt();
        int out2 = 3; //scanner.nextInt();

        MidiDeviceWrapper device1 = new MidiDeviceWrapper(devices[in1], devices[out1]);
        MidiDeviceWrapper device2 = new MidiDeviceWrapper(devices[in2], devices[out2]);

        devices[in1].open();
        devices[in2].open();
        devices[out1].open();
        devices[out2].open();

        LaunchpadInstance launchpadInstance = new LaunchpadInstance(device1, device2);
        LayoutManager manager = new LayoutManager(launchpadInstance);

        LaunchpadLayoutView view1 = new LaunchpadLayoutView(1, (byte) 95);
        LaunchpadLayoutView view2 = new LaunchpadLayoutView(22, (byte) 98);
        manager.addView("w1", view1);
        manager.addView("w2", view2);

        while (true) {
            Thread.sleep(2000);
        }
    }
}
