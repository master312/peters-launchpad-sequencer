
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

import midi.MidiDeviceInformer;
import midi.MidiDeviceManager;
import midi.MidiDeviceWrapper;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MidiDevice[] devices = MidiDeviceInformer.getDevices();
        System.out.println("Select device: ");

        for (int i = 0; i < devices.length; i++) {
            MidiDevice.Info info = devices[i].getDeviceInfo();
            System.out.println(i + ") " + info.getName() + " " + info.getDescription() + " | " + MidiDeviceInformer.getDeviceType(devices[i]));
        }

        System.out.println("-- -- -- -- -- -- -- -- -- --");
        System.out.println("Select device number: \n");

        Scanner scanner = new Scanner(System.in);
        int value = scanner.nextInt();
        scanner.close();

        MidiDeviceWrapper deviceWrapper = MidiDeviceManager.instance.OpenDevice(devices[value].getDeviceInfo());
        System.out.println(deviceWrapper);

        deviceWrapper.getMidiTransmitter().setReceiver(new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                System.out.println(Arrays.toString(message.getMessage()));
            }

            @Override
            public void close() {
            }
        });
    }
}
