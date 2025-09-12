package com.wu.achievers.BugTracking.repository;

import com.wu.achievers.BugTracking.entity.Bug;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BugRepoTest {

    @Autowired
    private BugRepo bugRepo;

    @Test
    void testSaveAndFindByAssigneeId() {
        Bug bug = new Bug();
        bug.setAssignedTo(1L);
        bugRepo.save(bug);
        List<Bug> bugs = bugRepo.findByAssigneeId(1L);
        assertFalse(bugs.isEmpty());
        assertEquals(1L, bugs.get(0).getAssignedTo());
    }
}
