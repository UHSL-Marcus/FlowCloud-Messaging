package com.uhsl.flowmessage.flowmessagev2.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;

import com.imgtec.flow.client.core.NetworkException;
import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by Marcus on 22/02/2016.
 */
public class BackgroundTask {

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


   public static <RESULT> void call(final AsyncCall<RESULT> asyncCall, final Callback<RESULT> callback, final int task) {

       final FutureTask<RESULT> futureTask = new FutureTask<RESULT>(new Callable<RESULT>() {
           @Override
           public RESULT call() throws Exception {
               if (Looper.myLooper() == null)
                   Looper.prepare();

               return asyncCall.call();
           }
       });
       executorService.execute(futureTask);

       new Thread(new Runnable() {
           @Override
           public void run() {
               RESULT result = null;
               try {
                   result = futureTask.get();
                   callback.onBackGroundTaskResult(result, task);

               } catch (ExecutionException e) {
                   System.out.println("Execution Exception");
               } catch (Exception e) {
                   System.out.println("other Exception");
               }
           }
       }).start();

   }

    public static void run(final AsyncRun asyncRun){
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                try {
                    asyncRun.run();
                } catch (Exception e) {
                    System.out.println("Threaded Exception: " + e.toString() + " -> " + e.getMessage());
                }
            }
        };
        executorService.execute(run);

    }

    public interface Callback<RESULT> {
        void onBackGroundTaskResult(RESULT result, int task);
    }


}