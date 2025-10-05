package general;

import categories.*;
import projects.*;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DocumentedCategoriesApiTest.class,
        UndocumentedCategoriesApiTest.class,
        DocumentedCategoriesIdApiTest.class,
        UndocumentedCategoriesIdApiTest.class,
        ExpectedBehaviourFailingCategoriesIdApiTest.class,
        ActualBehaviourWorkingCategoriesApiTest.class,
        DocumentedProjectsApiTest.class,
        DocumentedProjectsIdApiTest.class,
        UndocumentedProjectsIdApiTest.class,
        UndocumentedProjectsApiTest.class
})
public class AllApiTests {
}
