import main.controllers.ApiAuthControllerTest;
import main.controllers.ApiGeneralControllerTest;
import main.controllers.ApiPostControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ApiAuthControllerTest.class, ApiPostControllerTest.class, ApiGeneralControllerTest.class} )
public class SuiteTestClass {

}
