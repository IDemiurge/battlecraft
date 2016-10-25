package main.system.threading;

import main.system.auxiliary.Chronos;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Weaver {
    protected static long delay = 0;
    protected static int threadN = 0;
    protected static Object obj;

    public static void inNewThread(long delay1, final Object object,
                                   final Method m, final Object... args) {
        delay = delay1;
        inNewThread(object, m, args);
    }

    public static void inNewThread(final Object object, String methodname,
                                   final Object arg, final Class<?> param) {
        Method m = null;
        try {
            m = object.getClass().getMethod(methodname, param);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (m == null) {
            main.system.auxiliary.LogMaster.log(4, "failed to find method: "
                    + methodname);
            return;
        }
        inNewThread(object, m, arg);
    }

    public static void inNewThread(final Object object, String methodname,
                                   final Object... args) {
        Method m = null;
        try {
            m = object.getClass().getMethod(methodname, null);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (m == null) {
            main.system.auxiliary.LogMaster.log(4, "failed to find method: "
                    + methodname);
            return;
        }
        inNewThread(object, m, args);

    }

    public static void inNewThread(final Object object, final Method m,
                                   final Object... args) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                String name = m.getName() + threadN;
                Chronos.mark(name);

                try {
                    obj = m.invoke(object, args);
                } catch (Exception e) {
                    e.printStackTrace();
                    main.system.auxiliary.LogMaster.log(4,
                            "METHOD INVOCATION FAILED: " + m.getName()
                                    + " FOR ARGS "
                                    + Arrays.asList(args).toString()

                    );

                }
                String s = (args != null) ? Arrays.asList(args).toString()
                        : "nullargs";
                // logger.fatal("THREAD " + name + " FOR " + s
                // + " FINISHED: "
                // + Chronos.getTimeElapsedForMark(name));
                threadN++;
            }

        }, m.getName() + threadN);
        // try {
        // t.sleep(delay);
        // delay = 0;
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        try {
            t.start();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

    }

    public static void inNewThread(Runnable runnable) {
        inNewThread(null, runnable);
    }

    public static void inNewThread(Boolean priorityMinMax, Runnable runnable) {
        Thread thread = new Thread(runnable);
        if (priorityMinMax != null)
            try {
                thread.setPriority(priorityMinMax ? Thread.MIN_PRIORITY
                        : Thread.MAX_PRIORITY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        thread.start();
    }

}