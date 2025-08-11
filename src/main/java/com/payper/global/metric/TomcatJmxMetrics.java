package com.payper.global.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class TomcatJmxMetrics implements MeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            // 스레드 풀 (예: http-nio-8080)
            ObjectName threadPool = new ObjectName("Catalina:type=ThreadPool,name=\"http-nio-8080\"");
            registry.gauge("tomcat.threads.current",
                    mBeanServer,
                    mbs -> getIntegerAttribute(mbs, threadPool, "currentThreadCount"));

            registry.gauge("tomcat.threads.busy",
                    mBeanServer,
                    mbs -> getIntegerAttribute(mbs, threadPool, "currentThreadsBusy"));

            // 세션 매니저 (예: context=/, host=localhost)
            ObjectName manager = new ObjectName("Catalina:type=Manager,context=/,host=localhost");
            registry.gauge("tomcat.sessions.active",
                    mBeanServer,
                    mbs -> getIntegerAttribute(mbs, manager, "activeSessions"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getIntegerAttribute(MBeanServer mBeanServer, ObjectName objectName, String attribute) {
        try {
            Object value = mBeanServer.getAttribute(objectName, attribute);
            if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
