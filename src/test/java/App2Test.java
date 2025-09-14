

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class App2Test {
    private App app;

    @Before
    public void setup() {
        app = new App();
    }

    @Test
    public void testSayHello() {
    	 assertEquals("Hallo",app.sayHello());	    
    }
}
