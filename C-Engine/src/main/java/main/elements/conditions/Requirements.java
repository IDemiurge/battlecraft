package main.elements.conditions;

import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.Err;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Requirements implements Condition {

    protected Map<String, Condition> reqMap = new XLinkedMap<String, Condition>();
    List<String> reasons = new LinkedList<>();
    private String reason;
    private boolean fullCheck = true;

    public Requirements(Conditions conditions, String... text) {
        if (conditions.size() != text.length) {
            Err.error("Number of args for Requirements map does not match!");
        }
        int i = 0;
        for (Condition c : conditions) {
            reqMap.put(text[i], c);
            i++;
        }
    }

    public Requirements(List<Condition> list, String... text) {
        this(new Conditions(list), text);
    }

    public Requirements(Map<String, Condition> reqMap) {
        this.reqMap = reqMap;
    }

    public Requirements() {

    }

    public Requirements(Requirement... req) {
        for (Requirement r : req) {
            add(r);
        }

    }

    @Override
    public String toString() {
        return reqMap.toString();
    }

    public String getInfoStrings() {
        String string = "";
        for (String req : reqMap.keySet()) {
            string += req + ", ";
        }
        return StringMaster.cropLast(string, 2);
    }

    public void addAll(Requirements req) {
        reqMap.putAll(req.getReqMap());
    }

    public void add(Requirement r) {
        reqMap.put(r.getText(), r.getCondition());
    }

    public String checkReason(Ref ref, Entity match) {
        ref.setMatch(match.getId());
        return checkReason(ref);
    }

    public String checkReason(Ref ref) {
        if (check(ref, false))
            return null;
        else
            return getReason();

    }

    public boolean check(Ref ref) {
        reasons.clear();
        Ref REF = ref.getCopy();
        REF.setValue(KEYS.PAYEE, REF.getSource() + "");
        setReason(null);
        for (String r : reqMap.keySet()) {
            if (!reqMap.get(r).check(REF)) {
                reasons.add(r);
                if (getReason() == null)
                    setReason(r);
                if (!isFullCheck())
                    return false;
            }
        }
        return getReason() == null;
    }

    public boolean check(Ref ref, boolean forceFullCheck) {
        boolean buffer = isFullCheck();
        setFullCheck(forceFullCheck);
        boolean result = false;
        try {
            result = check(ref);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setFullCheck(buffer);

        }
        return result;
    }

    public boolean isFullCheck() {
        return fullCheck;
    }

    public void setFullCheck(boolean fullCheck) {
        this.fullCheck = fullCheck;
    }

    @Override
    public Ref getRef() {
        return null;
    }

    @Override
    public void setRef(Ref ref) {
    }

    @Override
    public String getTooltip() {
        return StringMaster.cropByLength(ConditionImpl.MAX_TOOLTIP_LENGTH, reqMap.values()
                .toString());
    }

    @Override
    public boolean check() {
        return check(new Ref());
    }

    @Override
    public boolean check(Entity match) {
        return check(new Ref(match));
    }

    public synchronized Map<String, Condition> getReqMap() {
        return reqMap;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public Condition join(Condition condition) {
        add(new Requirement(condition, "reason: " + condition.toString()));
        return this;
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    public List<String> getReasons() {
        return reasons;
    }

}