package com.dj.spider;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class CallableThreadTest {
    public static void main(String[] args) {
        Callable<Integer> callable = new Callable<Integer>() {
            public Integer call() throws Exception {
                int i = 999;
                return i;
            }
        };

        FutureTask<Integer> futureTask = new FutureTask<Integer>(callable);
        new Thread(futureTask).start();

        try
        {
            Thread.sleep(5000);
            System.out.println(futureTask.get());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
