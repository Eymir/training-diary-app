package ru.td.portal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 12/18/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
        "classpath:spring/test-context.xml")
public class DatabaseTest {


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void test() {
//        String id = jdbcTemplate.queryForObject("select top 1 id from JBT_MEM", String.class);
//        assertTrue(id.equals("20"));
        assertTrue(jdbcTemplate.queryForInt("select count (*) from Measure") == 9);
    }
}
