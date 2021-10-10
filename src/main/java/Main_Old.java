
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

import launchpad.*;
import midi.MidiDeviceInformer;
import midi.MidiDeviceWrapper;

import javax.sound.midi.*;
import java.util.Scanner;

public class Main_Old {
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
        // DAW
        int in1 = 4;
        int out1 = 2;

        // Custom Midi
        int in2 = 5;
        int out2 = 3;


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
        LaunchpadLayoutView view1 = new LaunchpadLayoutView(1, (byte) 95);
        LaunchpadLayoutView view2 = new LaunchpadLayoutView(22, (byte) 98);

        LayoutManager manager = new LayoutManager(launchpadInstance);

        manager.addView("w1", view1);
        manager.addView("w2", view2);

        int cnt = 1;
        // Test
        int iss = 0;
        while (true) {
            if (cnt > 8) {
                cnt = 1;
            }

            byte color = (byte)(int)((Math.random() * 126) + 1);
            int x = cnt, y = cnt;
            launchpadInstance.colorOff(x, y);
            cnt++;

            int var = iss < 2 ? scanner.nextInt() : -1;
            iss ++;
            switch (var)
            {
                case 1:
                    launchpadInstance.exitProgrammersMode();
                    break;
                case 2:
                    launchpadInstance.exitProgrammersMode();
                    break;
                case 6:
                    // 00h (0): Session (only selectable in DAW mode)
                    launchpadInstance.selectLayout(LaunchpadLayouts.DawSession);
                    break;
                case 7:
                    launchpadInstance.selectLayout(LaunchpadLayouts.DrumRack);
                    break;
                case 8:
                    launchpadInstance.selectLayout(LaunchpadLayouts.Keys);
                    break;
                case 9:
                    launchpadInstance.selectLayout(LaunchpadLayouts.DawFaders);
                    break;
                case 10:
                    // User
                    launchpadInstance.selectLayout(LaunchpadLayouts.UserMode);
                    break;
                case 11:
                    // 7Fh (127): Programmer mode
                    launchpadInstance.selectLayout(LaunchpadLayouts.ProgrammersMode);
                    break;
            }

            Thread.sleep(40);
            for (int i = y + 1; i <= 8; i++) {
                launchpadInstance.setColor(x, i, color,  LaunchpadColorType.Static);
                Thread.sleep(50);
            }

            for (int i = y - 1; i > 0 ; i--) {
                launchpadInstance.setColor(x, i, color,  LaunchpadColorType.Static);
                Thread.sleep(50);
            }
        }
    }
}
