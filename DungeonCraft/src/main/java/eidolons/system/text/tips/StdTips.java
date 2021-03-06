package eidolons.system.text.tips;

import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

public enum  StdTips implements TxtTip{
    fallen,

    ;
    @Override
    public String getMapId() {
        return  "std";
    }

    private boolean done;
    private String img; //TODO what about this one?
    private final WaitMaster.WAIT_OPERATIONS messageChannel = MESSAGE_RESPONSE;

    @Override
    public boolean isOptional() {
        boolean optional = true;
        return optional;
    }

    @Override
    public boolean isOnce() {
        boolean once = true;
        return once;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public String getImg() {
        return img;
    }


    @Override
    public WaitMaster.WAIT_OPERATIONS getMessageChannel() {
        return messageChannel;
    }

    @Override
    public void run() {

    }
}
