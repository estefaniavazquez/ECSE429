package general;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

import categories.ActualBehaviourWorkingCategoriesIdApiTest;
import categories.DocumentedCategoriesApiTest;
import categories.DocumentedCategoriesIdApiTest;
import categories.ExpectedBehaviourFailingCategoriesIdApiTest;
import categories.UndocumentedCategoriesApiTest;
import categories.UndocumentedCategoriesIdApiTest;
import interoperability.CrossRelationshipInteropApiTest;
import interoperability.DocumentedInteropApiTest;
import interoperability.DocumentedInteropXmlApiTest;
import interoperability.ExpectedBehaviourFailingInteropApiTest;
import interoperability.ExpectedBehaviourFailingInteropXmlApiTest;
import interoperability.UndocumentedInteropApiTest;
import interoperability.UndocumentedInteropXmlApiTest;
import projects.DocumentedProjectsApiTest;
import projects.DocumentedProjectsIdApiTest;
import projects.UndocumentedProjectsApiTest;
import projects.UndocumentedProjectsIdApiTest;
import todos.DocumentedTodosApiTest;
import todos.DocumentedTodosIdApiTest;
import todos.UndocumentedTodosApiTest;
import todos.UndocumentedTodosIdApiTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DocumentedTodosApiTest.class,
        DocumentedProjectsApiTest.class,
        DocumentedCategoriesApiTest.class,
        DocumentedInteropApiTest.class,
        DocumentedInteropXmlApiTest.class,
        CrossRelationshipInteropApiTest.class,

        DocumentedTodosIdApiTest.class,
        DocumentedProjectsIdApiTest.class,
        DocumentedCategoriesIdApiTest.class,

        UndocumentedTodosApiTest.class,
        UndocumentedProjectsApiTest.class,
        UndocumentedCategoriesApiTest.class,
        UndocumentedInteropApiTest.class,
        UndocumentedInteropXmlApiTest.class,

        UndocumentedTodosIdApiTest.class,
        UndocumentedProjectsIdApiTest.class,
        UndocumentedCategoriesIdApiTest.class,

        ExpectedBehaviourFailingCategoriesIdApiTest.class,
        ExpectedBehaviourFailingInteropApiTest.class,
        ExpectedBehaviourFailingInteropXmlApiTest.class,

        ActualBehaviourWorkingCategoriesIdApiTest.class,
})

public class AllApiTests {
}
