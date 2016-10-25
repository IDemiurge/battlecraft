package main.system.threading;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskMaster {

    public static Timer newTimer(Object object, final String methodName, Class<?>[] params,
                                 final Object[] args, long period) {

        Timer timer = new Timer();
        try {
            final Method method = object.getClass().getMethod(methodName, params);
            if (method == null) {
                main.system.auxiliary.LogMaster.log(1, "*** Timer cannot find method: "
                        + methodName);
                return null;
            }
            main.system.auxiliary.LogMaster.log(1, "Timer started for " + methodName);

            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    try {
                        main.system.auxiliary.LogMaster.log(0, "Invoking " + methodName);
                        if (args == null)
                            method.invoke(null);
                        else
                            method.invoke(args);

                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }

            };
            timer.schedule(task, period, period);

        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return timer;
        }
        return timer;
    }

}