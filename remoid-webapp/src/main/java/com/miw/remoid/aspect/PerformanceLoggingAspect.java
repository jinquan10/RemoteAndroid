package com.miw.remoid.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Aspect
public class PerformanceLoggingAspect {
//    private static EtmMonitor etmMonitor = null;
//
//    private static SimpleTextRendererLog simpleTextRendererLog = null;
//    
//    private int numTimes = 0;
//    private int interval = 1000;
//    
//    static {
//        getTextRenderer();
//        configureEtm();
//    }
//
//    private static void getTextRenderer(){
//        simpleTextRendererLog = new SimpleTextRendererLog();
//    }
//    
//    private static void configureEtm() {
//        BasicEtmConfigurator.configure();
//
//        try {
//            etmMonitor = EtmManager.getEtmMonitor();
//            etmMonitor.start();
//        } catch (Exception e) {
//
//        }
//    }
//
//    @Around("execution(* com.nwm.coauthor.service.controller..*.*(..))")
//    public Object collectMetricsForMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        if (!(simpleTextRendererLog.isLog4jWriter())) {
//            return proceedingJoinPoint.proceed();
//        }
//
//        EtmPoint etmPoint = null;
//        Object result = null;
//
//        // input sanity test
//        if (proceedingJoinPoint == null) {
//            throw new RuntimeException("aop error, join point is null for collectMetricsForMethod");
//        }
//
//        try {
//            // start timing here. We don't give the point a name because we're
//            // waiting to see if it succeeds.
//            etmPoint = etmMonitor.createPoint(null);
//
//            // this is calling the method we're profiling.
//            result = proceedingJoinPoint.proceed();
//
//            // if we get here, there was no exception thrown, so we add the word
//            // "success" to the
//            // mbean name.
//            etmPoint.alterName(proceedingJoinPoint.toShortString() + " (success)");
//        } catch (Throwable t) {
//
//            // if we get here, presumably the method we're profiling threw an
//            // exception, so we add
//            // the word "failure" to the mbean name
//            if (etmPoint != null) {
//                etmPoint.alterName(proceedingJoinPoint.toShortString() + " (failure)");
//            }
//
//            // profiling should not affect program execution, so we rethrow the
//            // exception we got
//            // to keep behavior the same.
//            throw t;
//        } finally {
//
//            // finishing profiling is put in a finally block, this works whether
//            // or not an exception occurs.
//            if (etmPoint != null) {
//                etmPoint.collect();
//                numTimes++;
//                
//                if(numTimes > interval){
//                    etmMonitor.render(simpleTextRendererLog);
//                    numTimes = 0;
//                }
//            }
//        }
//
//        // if no exception, return whatever value the profiled method returned.
//        return result;
//    }
}
