package general;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

import interoperability.ComprehensiveApiBehaviourTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Suite.class)
@Suite.SuiteClasses({
                // DocumentedCategoriesApiTest.class,
                // UndocumentedCategoriesApiTest.class,
                // DocumentedCategoriesIdApiTest.class,
                // UndocumentedCategoriesIdApiTest.class,
                // ExpectedBehaviourFailingCategoriesIdApiTest.class,
                // ActualBehaviourWorkingCategoriesApiTest.class,
                // DocumentedProjectsApiTest.class,
                // DocumentedProjectsIdApiTest.class,
                // UndocumentedProjectsIdApiTest.class,
                // UndocumentedProjectsApiTest.class,
                // ====================================
                // DocumentedInteropApiTest.class,
                // UndocumentedInteropApiTest.class,
                // ExpectedBehaviourFailingInteropApiTest.class,
                // CrossRelationshipInteropApiTest.class,
                // ====================================
                // DocumentedInteropXmlApiTest.class,
                // UndocumentedInteropXmlApiTest.class,
                // ExpectedBehaviourFailingInteropXmlApiTest.class
                // ====================================
                ComprehensiveApiBehaviourTest.class
})
public class AllApiTests {
}
