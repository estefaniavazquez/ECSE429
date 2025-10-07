package general;

import categories.*;

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
        ActualBehaviourWorkingCategoriesIdApiTest.class
})
public class AllApiTests {}
