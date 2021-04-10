package session;

import framework.C3Handler;
import framework.C3Manager;
import main.system.datatypes.DequeImpl;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.util.TimerTask;

public class C3TimerHandler extends C3Handler {
    DequeImpl<C3Timer> keyTimers = new DequeImpl();
    private boolean testMode=false;

    public C3TimerHandler(C3Manager manager) {
        super(manager);
    }

    public void playSystemSound(){
        final Runnable runnable =
                (Runnable) Toolkit.getDefaultToolkit().
                        getDesktopProperty("win.sound.exclamation");
        if (runnable != null)
            runnable.run();
    }

    public void initTimer(C3Session session) {
        Integer minutesTotal = session.getDuration();
                keyTimers.add(initKeyTimer(minutesTotal, session));
    }

    public void timerDone(C3Timer timer) {
        keyTimers.remove(timer);
    }
    private C3Timer initKeyTimer(int minutesTotal, C3Session session) {
        int key= NativeKeyEvent.VC_PAGE_DOWN;
        int mod= NativeKeyEvent.CTRL_MASK;
        long delay= 15*60*1000;
        long limit=minutesTotal*60*1000;
        if (testMode)
        {
            delay = 3000;
            limit = 9000;
        }
        return new C3Timer(this, key, mod, ()-> new TimerTask() {
            @Override
            public void run() {
                manager.getTrayHandler().notify("Take a break!", "C3");
                playSystemSound();
            }
        }, delay, limit, session).init();
    }


    public boolean checkTimers(int modifiers, int keyCode) {
        for (C3Timer keyTimer : keyTimers) {
            if (keyTimer.check(keyCode, modifiers)) {
                return true;
            }
        }

        return false;
    }
}
