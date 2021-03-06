package eidolons.ability.conditions;

import main.data.ability.OmittedConstructor;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;

import java.util.function.Predicate;

public class DynamicCondition<T> extends ConditionImpl {
    private Predicate<T> predicate;
    private T arg;

    @OmittedConstructor
    public DynamicCondition(Predicate<T> predicate, T arg) {
        this.predicate = predicate;
        this.arg = arg;
    }

    @OmittedConstructor
    public DynamicCondition(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @OmittedConstructor
    public DynamicCondition() {
    }

    @Override
    public boolean check(Ref ref) {
        if (predicate != null) {
            if (arg == null) {
                if (ref.getMatchObj() != null) { //TODO this is dubious
                    return predicate.test((T) ref.getMatchObj());
                }
                if (ref.getTarget() != null) {
                    try {
                        return predicate.test((T) ref.getTargetObj());
                    } catch (Exception e) {
                        try {
                            return predicate.test((T) ref.getTargetObj().getType());
                        } catch (Exception e1) {
                            main.system.ExceptionMaster.printStackTrace(e1);
                        }
                    }
                }
            } else
                return predicate.test(arg);
        }
        return false;
    }

}
