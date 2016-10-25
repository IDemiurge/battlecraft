package main.elements.conditions;

public class RefNotEmptyCondition extends MicroCondition {
    private String obj;
    private String key;

    public RefNotEmptyCondition(String ref1, String ref2) {
        this.obj = ref1;
        this.key = ref2;

    }

    @Override
    public String toString() {
        return super.toString() + ": " + obj + "'s " + key;
    }

    @Override
    public boolean check() {
        try {
            return ref.getObj(obj).getRef().getObj(key) != null;
        } catch (Exception e) {
            main.system.auxiliary.LogMaster.log(1, toString() + " failed on "
                    + ref);
        }
        return false;
    }
}