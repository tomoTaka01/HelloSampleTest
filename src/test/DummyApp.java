/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Web Test Application
 * 
 * @author tomo
 */
public class DummyApp extends Application {
    private static WebEngine engine;
    private static DummyApp app;
    private static BlockingQueue<DummyApp> instance = new LinkedBlockingDeque<>();
    private BlockingQueue<Map<String, String>> arguments = new LinkedBlockingDeque<>();
    private BlockingQueue<Map<String, String>> results = new LinkedBlockingDeque<>();
    
    @Override
    public void start(Stage primaryStage) {
        app = this;
        engine = new WebEngine();
        addListner();
        engine.load("http://localhost:8080/HelloSample/faces/Hello.xhtml");
    }
    
    public static DummyApp getInstance() throws InterruptedException {
        return instance.take();
    }
    
    public void shutDown() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Platform.exit();
            }
        });
    }
    
    public Map<String, String> HelloTest(
            String name, String greetSel, String lang) throws InterruptedException {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("greetSel", greetSel);
        params.put("language", lang);
        arguments.put(params);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!engine.getTitle().equalsIgnoreCase("Hello")) {
                        engine.load("http://localhost:8080/HelloSample/faces/Hello.xhtml");
                        instance.take();
                    }
                    Map<String, String> params = arguments.take();
                    String nameVal = params.get("name");
                    String selVal = params.get("greetSel");
                    String langVal = params.get("language");
                    engine.executeScript("document.getElementById('form1:name').value = '" + nameVal + "'");
                    engine.executeScript("document.getElementsByName('form1:greetSel')[" + selVal + "].checked = true");
                    engine.executeScript("document.getElementById('form1:language').options[" + langVal + "].selected = true");
                    engine.executeScript("var btn = document.getElementById('form1:greet'); btn.click();");
                } catch (InterruptedException ex) {
                    Logger.getLogger(DummyApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return results.take();
    }

    private void addListner() {
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                if (newState == State.SUCCEEDED && engine.getTitle().equalsIgnoreCase("Hello")) {
                    try {
                        instance.put(app);
                    } catch (InterruptedException ex) {}
                }
                if (newState == State.SUCCEEDED && engine.getTitle().equalsIgnoreCase("Greeting")) {
                    Document doc = engine.getDocument();
                    NodeList nodes = doc.getElementsByTagName("H1");
                    Node h1Node = nodes.item(0);
                    Map<String, String> result = new HashMap<>();
                    result.put("h1", h1Node.getTextContent());
                    try {
                        results.put(result);
                    } catch (InterruptedException ex) {}
                }
            }
        });
    }
            
}
