package com.tgbus.servermerger.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-19
 * Time: 10:42:14
 * To change this template use File | Settings | File Templates.
 */
public class SpringHelper implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public <T> T getBean(String beanName, Class<? extends T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        Object obj = applicationContext.getBean(beanName);
        if (Util.instanceOf(obj, clazz)) {
            return (T) obj;
        } else {
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
