package ca.wisecode.lucene.master.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/23/2024 1:44 AM
 * @Version: 1.0
 * @description:
 */

public class SlaveStatusChangeEvent extends ApplicationEvent {

    public SlaveStatusChangeEvent(Object source) {
        super(source);
    }
}
