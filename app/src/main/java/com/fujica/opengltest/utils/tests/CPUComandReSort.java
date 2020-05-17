package com.fujica.opengltest.utils.tests;

import android.util.Log;

public class CPUComandReSort {
    /**
     * 测试CPU指令重排序
     * 结果1：1364次时出现x=0,y=0
     * 结果2：8109次时出现x=0,y=0
     * 结果3：8171次时出现x=0,y=0
     * 结果4：1700次时出现x=0,y=0
     */
    public static void testCpuComandResort(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                int times = 0;
                Log.e("TAG", "start run");
                while(running){
                    times ++;
                    if(times % 100 == 0){
                        Log.e("TAG", "run " + times + " times");
                    }
                    final int[] a = {0};
                    final int[] b = { 0 };
                    final int[] x = new int[1];
                    final int[] y = new int[1];
                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(0);
                            }
                            catch (Exception e){ }
                            a[0] = 1;
                            x[0] = b[0];
                        }
                    });
                    Thread t2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(0);
                            }
                            catch (Exception e){ }
                            b[0] = 1;
                            y[0] = a[0];
                        }
                    });
                    t1.start();
                    t2.start();
                    try {
                        t1.join();
                        t2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(y[0] == 0 && x[0] == 0){
                        running = false;
                        Log.e("TAG", "x = 0 & y = 0 in " + times);
                    }
                }
            }
        }).start();
    }
}
