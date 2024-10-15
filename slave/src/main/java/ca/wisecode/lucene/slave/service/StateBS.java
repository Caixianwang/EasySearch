package ca.wisecode.lucene.slave.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/30/2024 9:49 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class StateBS {

    private int newAddIndexSize = 0;

    public int getNewAddIndexSize() {
        return newAddIndexSize;
    }

    public void setNewAddIndexSize(int newAddIndexSize) {
        this.newAddIndexSize = newAddIndexSize;
    }
}
