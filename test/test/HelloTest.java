/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import static org.hamcrest.CoreMatchers.is;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author tomo
 */
public class HelloTest {
    private static DummyApp dummyApp;
    private static ExecutorService service;
    
    public HelloTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws InterruptedException {
        service = Executors.newFixedThreadPool(1);
        service.submit(new Runnable() {
            @Override
            public void run() {
                Application.launch(test.DummyApp.class);
            }
        });
        dummyApp = DummyApp.getInstance();
    }
    
    @AfterClass
    public static void tearDownClass() {
        dummyApp.shutDown();
    }
    
     @Test
     public void hello() throws InterruptedException {
        Map<String, String> results = dummyApp.HelloTest("JavaFX", "0", "0");
        String result = results.get("h1");
        assertThat(result, is("Hello JavaFX"));        
     }

//     @Test
//     public void おはよう() throws InterruptedException {
//        Map<String, String> results = dummyApp.HelloTest("JavaFX", "1", "1");
//        String result = results.get("h1");
//        assertThat(result, is("おはよう JavaFX"));        
//     }
}
