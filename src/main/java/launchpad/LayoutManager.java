package launchpad;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Hashtable;

// Handles layout managing in programmers mode
public class LayoutManager implements Receiver {

    private final static byte ActiveButtonColor = 0x21;

    private LaunchpadInstance instance;

    private LaunchpadLayoutView activeView;

    private final Hashtable<String, LaunchpadLayoutView> views = new Hashtable<>();

    public LayoutManager(LaunchpadInstance instance) {
        this.instance = instance;
        this.instance.enterProgrammersMode();
        this.instance.setProgrammersModeReceiver(this);
        this.instance.clearProgrammersState();
    }

    public void activateView(String layoutName) {
        LaunchpadLayoutView view = this.views.get(layoutName);
        this.activateView(view);
    }

    private void activateView(LaunchpadLayoutView view) {
        ColorSpecMsg msg = new ColorSpecMsg(null);
        if (view != null && this.activeView != view) {
            if (this.activeView != null) {
                this.activeView.onHide();
                byte activationButton = this.activeView.getActivationButton();
                if (activationButton != 0) {
                    msg = msg.linkMessage(LaunchpadColorType.Static, activationButton, ActiveButtonColor);
                }
            }

            this.activeView = view;
            this.activeView.onShow();
            byte activationButton = this.activeView.getActivationButton();
            if (activationButton != 0) {
                msg = msg.linkMessage(LaunchpadColorType.Pulsing, activationButton, ActiveButtonColor);
            }

        } else if (view == null && this.activeView != null) {
            this.activeView.onHide();
            byte activationButton = this.activeView.getActivationButton();
            if (activationButton != 0) {
                msg = msg.linkMessage(LaunchpadColorType.Static, activationButton, ActiveButtonColor);
            }

            this.activeView = null;
        }

        if (msg != null && msg.getMessage() != null) {
            this.instance.fireColorSpecMsg(msg);
        }

        this.redrawActiveView(true);
    }

    private void redrawActiveView(boolean force) {
        if (this.activeView != null) {
            this.activeView.drawGrid(this.instance, force);
        }
    }

    public void addView(String name, LaunchpadLayoutView view) {
        this.views.put(name, view);
        if (view.getActivationButton() != 0) {
            ColorSpecMsg msg = new ColorSpecMsg(null);
            this.instance.fireColorSpecMsg(msg.linkMessage(LaunchpadColorType.Static, view.getActivationButton(), ActiveButtonColor));
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message.getMessage().length < 3) {
            return;
        }

        switch (message.getMessage()[0]) {
            case -80:
                // Control key pressed
                // Try to change view
                byte keyPressed = message.getMessage()[1];
                for (String key : this.views.keySet()) {
                    LaunchpadLayoutView view = this.views.get(key);
                    if (view.getActivationButton() == keyPressed) {
                        this.activateView(view);
                        return;
                    }
                }
            case -112:
                byte position = message.getMessage()[1];
                byte isPressed = message.getMessage()[2];
                if (this.activeView == null) {
                    return;
                }

                if (isPressed != 127) {
                    // button released!
                    return;
                }

                int[] pos = instance.byteToPosition(position);
                this.activeView.onButtonPressed(pos[0], pos[1]);
                this.redrawActiveView(false);
                break;
        }
    }

    @Override
    public void close() {

    }
}
