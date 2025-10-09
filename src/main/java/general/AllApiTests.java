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
                DocumentedCategoriesApiTest.class,
                UndocumentedCategoriesApiTest.class,
                DocumentedCategoriesIdApiTest.class,
                UndocumentedCategoriesIdApiTest.class,
                ExpectedBehaviourFailingCategoriesIdApiTest.class,
                ActualBehaviourWorkingCategoriesIdApiTest.class,
                DocumentedProjectsApiTest.class,
                DocumentedProjectsIdApiTest.class,
                UndocumentedProjectsIdApiTest.class,
                UndocumentedProjectsApiTest.class,
                DocumentedInteropApiTest.class,
                UndocumentedInteropApiTest.class,
                ExpectedBehaviourFailingInteropApiTest.class,
                CrossRelationshipInteropApiTest.class,
                DocumentedInteropXmlApiTest.class,
                UndocumentedInteropXmlApiTest.class,
                ExpectedBehaviourFailingInteropXmlApiTest.class,
                ActualBehaviourWorkingCategoriesIdApiTest.class,
                DocumentedProjectsApiTest.class,
                DocumentedProjectsIdApiTest.class,
                UndocumentedProjectsIdApiTest.class,
                UndocumentedProjectsApiTest.class
        DocumentedCategoriesApiTest.class,
        UndocumentedCategoriesApiTest.class,
        DocumentedCategoriesIdApiTest.class,
        UndocumentedCategoriesIdApiTest.class,
        ExpectedBehaviourFailingCategoriesIdApiTest.class,
        ActualBehaviourWorkingCategoriesIdApiTest.class,
        DocumentedProjectsApiTest.class,
        DocumentedProjectsIdApiTest.class,
        UndocumentedProjectsIdApiTest.class,
        UndocumentedProjectsApiTest.class,
        DocumentedInteropApiTest.class,
        UndocumentedInteropApiTest.class,
        ExpectedBehaviourFailingInteropApiTest.class,
        CrossRelationshipInteropApiTest.class,
        DocumentedInteropXmlApiTest.class,
        UndocumentedInteropXmlApiTest.class,
        ExpectedBehaviourFailingInteropXmlApiTest.class,
        ActualBehaviourWorkingCategoriesIdApiTest.class,
        DocumentedProjectsApiTest.class,
        DocumentedProjectsIdApiTest.class,
        UndocumentedProjectsIdApiTest.class,
        UndocumentedProjectsApiTest.class,
        DocumentedTodosApiTest.class,
        UndocumentedTodosApiTest.class,
        DocumentedTodosIdApiTest.class,
        UndocumentedTodosIdApiTest.class

})

public class AllApiTests {
}
